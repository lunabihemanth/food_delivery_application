import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-jeevitha',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './jeevitha.html',
  styleUrls: ['./jeevitha.css']
})
export class Jeevitha {

  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Jeevitha';
  role = 'Coupon & Rating API Tester';

  // ================= ENDPOINTS DISPLAY =================
  couponEndpoints = [
    { method: 'POST', path: '/coupons', desc: 'Create coupon' },
    { method: 'GET', path: '/coupons', desc: 'List all coupons' },
    { method: 'GET', path: '/coupons/{couponCode}', desc: 'Validate coupon' },
    { method: 'PUT', path: '/coupons/{couponId}', desc: 'Update coupon' },
    { method: 'DELETE', path: '/coupons/{couponId}', desc: 'Disable coupon' },
  ];

  orderCouponEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/coupons/{couponId}', desc: 'Apply coupon to order' },
    { method: 'DELETE', path: '/orders/{orderId}/coupons/{couponId}', desc: 'Remove applied coupon' },
    { method: 'GET', path: '/orders/{orderId}/coupons', desc: 'View coupons applied to an order' },
  ];

  ratingEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/ratings', desc: 'Submit rating for restaurant' },
    { method: 'GET', path: '/restaurants/{restaurantId}/ratings', desc: 'View all ratings for restaurant' },
    //{ method: 'GET', path: '/ratings/{ratingId}', desc: 'Fetch a rating' },
    { method: 'DELETE', path: '/ratings/{ratingId}', desc: 'Remove rating (admin/moderation)' },
  ];

  // ================= STATE =================
  coupons: any[] = [];
  appliedCoupons: any[] = [];
  ratings: any[] = [];
  singleCoupon: any = null;
  singleRating: any = null;

  // Coupon operations
  couponActionId = '';
  couponActionType = '';
  couponActionTitle = '';
  validateCouponCode = '';

  // Order context for order-coupon APIs
  contextOrderId = '';
  contextCouponIdForApply = '';

  // Rating context
  contextRestaurantIdForRatings = '';
  ratingActionId = '';
  ratingActionType = '';
  ratingActionTitle = '';

  // ✅ Updated to match backend DTO: couponCode, discountAmount, expiryDate
  newCoupon = {
    couponCode: '',
    discountAmount: 0,
    expiryDate: ''
  };

  newRating = {
    rating: 5,
    comment: ''
  };

  // ================= POPUP FLAGS =================
  showCouponsListPopup = false;
  showCouponFormPopup = false;
  showCouponIdPopup = false;
  showCouponDetailPopup = false;
  showValidateCouponPopup = false;

  showApplyCouponPopup = false;
  showRemoveCouponPopup = false;
  showAppliedCouponsPopup = false;

  showRatingsListPopup = false;
  showRatingFormPopup = false;
  showRatingIdPopup = false;
  showRatingDetailPopup = false;
  showRestaurantIdForRatingsPopup = false;

  // ================= AUTH =================
  authHeader() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Basic ' + btoa('admin:admin123'),
        'Content-Type': 'application/json'
      })
    };
  }

  safe(value: any): string {
    return value ? value.toString().trim() : '';
  }

  extractData(res: any): any[] {
    if (!res) return [];
    if (Array.isArray(res)) return res;
    if (res.data && Array.isArray(res.data)) return res.data;
    if (res.data) return [res.data];
    return [res];
  }

  showError(err: any) {
    alert(err?.error?.message ?? err?.message ?? 'An error occurred');
  }

  getMethodClass(method: string) {
    return {
      'bg-green-600': method === 'GET',
      'bg-orange-600': method === 'POST',
      'bg-yellow-600': method === 'PUT',
      'bg-red-600': method === 'DELETE'
    };
  }

  goBack() {
    this.location.back();
  }

  // ================= COUPON HANDLERS =================
  handleCoupon(ep: any) {
    const base = `${this.baseUrl}/coupons`;

    if (ep.method === 'POST') {
      this.resetCouponForm();
      this.couponActionType = 'POST';
      this.showCouponFormPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path === '/coupons') {
      this.http.get<any>(base, this.authHeader()).subscribe({
        next: (res) => {
          this.coupons = this.extractData(res);
          this.showCouponsListPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (ep.method === 'GET' && ep.path === '/coupons/{couponCode}') {
      this.validateCouponCode = '';
      this.showValidateCouponPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.couponActionType = 'PUT';
      this.couponActionTitle = 'Update Coupon – Enter ID';
      this.couponActionId = '';
      this.showCouponIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.couponActionType = 'DELETE';
      this.couponActionTitle = 'Disable Coupon – Enter ID';
      this.couponActionId = '';
      this.showCouponIdPopup = true;
      return;
    }
  }

  submitValidateCoupon() {
    const code = this.safe(this.validateCouponCode);
    if (!code) {
      alert('Coupon code is required');
      return;
    }
    const url = `${this.baseUrl}/coupons/${code}`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.singleCoupon = this.extractData(res)[0];
        this.showValidateCouponPopup = false;
        this.showCouponDetailPopup = true;
      },
      error: (err) => this.showError(err)
    });
  }

  submitCouponForm() {
    const id = this.safe(this.couponActionId);
    const base = `${this.baseUrl}/coupons`;

    // ✅ Build payload matching backend DTO
    const payload = {
      couponCode: this.newCoupon.couponCode,
      discountAmount: this.newCoupon.discountAmount,
      expiryDate: this.newCoupon.expiryDate
    };

    if (this.couponActionType === 'POST') {
      this.http.post(base, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Coupon Created ✅');
          this.closeCouponPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.couponActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Coupon Updated ✅');
          this.closeCouponPopups();
        },
        error: (err) => this.showError(err)
      });
    }
  }

  resetCouponForm() {
    this.newCoupon = {
      couponCode: '',
      discountAmount: 0,
      expiryDate: ''
    };
  }

  closeCouponPopups() {
    this.showCouponFormPopup = false;
    this.showCouponIdPopup = false;
    this.showCouponDetailPopup = false;
    this.showValidateCouponPopup = false;
    this.couponActionId = '';
    this.resetCouponForm();
  }

  // ================= ORDER-COUPON HANDLERS (unchanged) =================
  handleOrderCoupon(ep: any) {
    const oid = this.safe(this.contextOrderId);
    if (!oid) {
      alert('Please enter an Order ID (context) first');
      return;
    }

    if (ep.method === 'POST') {
      this.contextCouponIdForApply = '';
      this.showApplyCouponPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.contextCouponIdForApply = '';
      this.showRemoveCouponPopup = true;
      return;
    }

    if (ep.method === 'GET') {
      const url = `${this.baseUrl}/orders/${oid}/coupons`;
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.appliedCoupons = this.extractData(res);
          this.showAppliedCouponsPopup = true;
        },
        error: (err) => this.showError(err)
      });
    }
  }

  submitApplyCoupon() {
    const oid = this.safe(this.contextOrderId);
    const cid = this.safe(this.contextCouponIdForApply);
    if (!oid || !cid) {
      alert('Order ID and Coupon ID are required');
      return;
    }
    const url = `${this.baseUrl}/orders/${oid}/coupons/${cid}`;
    this.http.post(url, {}, this.authHeader()).subscribe({
      next: () => {
        alert('Coupon applied to order ✅');
        this.showApplyCouponPopup = false;
      },
      error: (err) => this.showError(err)
    });
  }

  submitRemoveCoupon() {
    const oid = this.safe(this.contextOrderId);
    const cid = this.safe(this.contextCouponIdForApply);
    if (!oid || !cid) {
      alert('Order ID and Coupon ID are required');
      return;
    }
    const url = `${this.baseUrl}/orders/${oid}/coupons/${cid}`;
    this.http.delete(url, this.authHeader()).subscribe({
      next: () => {
        alert('Coupon removed from order ✅');
        this.showRemoveCouponPopup = false;
      },
      error: (err) => this.showError(err)
    });
  }

  // ================= RATING HANDLERS (unchanged) =================
  handleRating(ep: any) {
    if (ep.method === 'POST') {
      const oid = this.safe(this.contextOrderId);
      if (!oid) {
        alert('Please enter an Order ID (context) first');
        return;
      }
      this.resetRatingForm();
      this.ratingActionType = 'POST';
      this.showRatingFormPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path.includes('/restaurants/')) {
      this.contextRestaurantIdForRatings = '';
      this.showRestaurantIdForRatingsPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path === '/ratings/{ratingId}') {
      this.ratingActionType = 'GET_BY_ID';
      this.ratingActionTitle = 'Fetch Rating – Enter ID';
      this.ratingActionId = '';
      this.showRatingIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.ratingActionType = 'DELETE';
      this.ratingActionTitle = 'Remove Rating – Enter ID';
      this.ratingActionId = '';
      this.showRatingIdPopup = true;
      return;
    }
  }

  confirmCouponIdAction() {
  const id = this.safe(this.couponActionId);
  const base = `${this.baseUrl}/coupons`;

  if (!id) {
    alert('Coupon ID is required');
    return;
  }

  // 👇 NEW: prevent accidental code input
  if (this.couponActionType === 'DELETE' && isNaN(Number(id))) {
    alert('Please enter a numeric Coupon ID (not the coupon code).');
    return;
  }

  if (this.couponActionType === 'DELETE') {
    this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
      next: () => {
        alert('Coupon disabled ✅');
        this.showCouponIdPopup = false;
      },
      error: (err) => this.showError(err)
    });
    return;
  }

  // PUT handling remains unchanged …
}

  confirmRatingIdAction() {
    const id = this.safe(this.ratingActionId);
    const base = `${this.baseUrl}/ratings`;

    if (!id) {
      alert('Rating ID is required');
      return;
    }

    if (this.ratingActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleRating = this.extractData(res)[0];
          this.showRatingIdPopup = false;
          this.showRatingDetailPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.ratingActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert('Rating removed ✅');
          this.showRatingIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
    }
  }

  submitRatingForm() {
  const oid = this.safe(this.contextOrderId);
  if (!oid) {
    alert('Order ID is required');
    return;
  }

  // 1. Fetch the order to get the restaurantId
  const orderUrl = `${this.baseUrl}/orders/${oid}`;
  this.http.get<any>(orderUrl, this.authHeader()).subscribe({
    next: (orderRes) => {
      const order = this.extractData(orderRes)[0];
      const restaurantId = order.restaurantId;   // adjust field name if needed

      // 2. Now send the rating with restaurantId
      const ratingUrl = `${this.baseUrl}/orders/${oid}/ratings`;
      const payload = {
        orderId: parseInt(oid),
        restaurantId: restaurantId,
        rating: this.newRating.rating,
        comment: this.newRating.comment
      };

      this.http.post(ratingUrl, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Rating submitted ✅');
          this.showRatingFormPopup = false;
        },
        error: (err) => this.showError(err)
      });
    },
    error: (err) => this.showError(err)
  });
}
  fetchRestaurantRatings() {
    const rid = this.safe(this.contextRestaurantIdForRatings);
    if (!rid) {
      alert('Restaurant ID is required');
      return;
    }
    const url = `${this.baseUrl}/restaurants/${rid}/ratings`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.ratings = this.extractData(res);
        this.showRestaurantIdForRatingsPopup = false;
        this.showRatingsListPopup = true;
      },
      error: (err) => this.showError(err)
    });
  }

  resetRatingForm() {
    this.newRating = { rating: 5, comment: '' };
  }

  closeRatingPopups() {
    this.showRatingFormPopup = false;
    this.showRatingIdPopup = false;
    this.showRatingDetailPopup = false;
    this.ratingActionId = '';
    this.resetRatingForm();
  }
}