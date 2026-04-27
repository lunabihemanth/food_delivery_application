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
  name = 'Jeevitha E';
  role = 'Coupon & Rating API Tester';

  // ─── Auth (matches backend user "jeevitha") ──────────
  private defaultAuth = btoa('admin:admin123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'jeevitha' && storedHeader) {
      return {
        headers: new HttpHeaders({
          Authorization: 'Basic ' + storedHeader,
          'Content-Type': 'application/json'
        })
      };
    }
    return {
      headers: new HttpHeaders({
        Authorization: 'Basic ' + this.defaultAuth,
        'Content-Type': 'application/json'
      })
    };
  }

  // ─── Endpoints ────────────────────────────────────────
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
    { method: 'DELETE', path: '/ratings/{ratingId}', desc: 'Remove rating (admin/moderation)' },
  ];

  // ─── State ────────────────────────────────────────────
  coupons: any[] = [];
  appliedCoupons: any[] = [];
  ratings: any[] = [];
  singleCoupon: any = null;
  singleRating: any = null;

  couponActionId = '';
  couponActionType = '';
  couponActionTitle = '';
  validateCouponCode = '';

  contextOrderId = '';                 // will be set via popup when needed
  contextCouponIdForApply = '';

  contextRestaurantIdForRatings = '';
  ratingActionId = '';
  ratingActionType = '';
  ratingActionTitle = '';

  newCoupon = {
    couponCode: '',
    discountAmount: 0,
    expiryDate: ''
  };

  newRating = {
    rating: 5,
    comment: ''
  };

  // Popup flags
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

  // New popup flags for dynamic order ID collection
  showOrderContextPopup = false;
  pendingOrderCouponEndpoint: any = null;   // for order‑coupon endpoints that require Order ID
  pendingRatingEndpoint: any = null;        // for rating endpoints that require Order ID

  // ─── Toast notification ───────────────────────────────
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  private showToastMessage(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 4000);
  }

  // ─── Helpers ──────────────────────────────────────────
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

  // ─── Unified endpoint handler ─────────────────────────
  handleEndpoint(ep: any) {
    // Coupon endpoints (no Order ID needed)
    if (ep.path.startsWith('/coupons')) {
      this.handleCoupon(ep);
      return;
    }

    // Order‑coupon endpoints (require Order ID)
    if (ep.path.includes('/orders') && ep.path.includes('coupons')) {
      this.pendingOrderCouponEndpoint = ep;
      this.pendingRatingEndpoint = null;
      this.contextOrderId = '';
      this.showOrderContextPopup = true;
      return;
    }

    // Rating endpoints that require Order ID (POST and maybe others)
    if (ep.path.includes('/orders/{orderId}/ratings')) {
      this.pendingRatingEndpoint = ep;
      this.pendingOrderCouponEndpoint = null;
      this.contextOrderId = '';
      this.showOrderContextPopup = true;
      return;
    }

    // Rating endpoints that don't require Order ID (GET by restaurant, DELETE by ratingId)
    this.handleRating(ep);
  }

  // Called when the user confirms the Order ID for order‑coupon or rating
  confirmOrderContext() {
    const oid = this.safe(this.contextOrderId);
    if (!oid) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }
    this.showOrderContextPopup = false;

    if (this.pendingOrderCouponEndpoint) {
      this.handleOrderCoupon(this.pendingOrderCouponEndpoint);
    } else if (this.pendingRatingEndpoint) {
      this.handleRating(this.pendingRatingEndpoint);
    }
  }

  // ─── Coupon handlers ──────────────────────────────────
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
          this.showToastMessage('Coupons loaded', 'success');
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
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

  confirmCouponIdAction() {
  const id = this.safe(this.couponActionId);
  const base = `${this.baseUrl}/coupons`;

  if (!id) {
    this.showToastMessage('Coupon ID is required', 'error');
    return;
  }

  if (this.couponActionType === 'DELETE') {
    if (isNaN(Number(id))) {
      this.showToastMessage('Please enter a numeric Coupon ID', 'error');
      return;
    }
    this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Coupon disabled ✅', 'success');
        this.showCouponIdPopup = false;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
    return;
  }

  if (this.couponActionType === 'PUT') {
    // Find the coupon from the already loaded list (no extra GET call)
    const coupon = this.coupons.find(c => c.couponId == id);
    if (!coupon) {
      // If list is empty or coupon not in it, fetch all coupons first, then try again
      if (this.coupons.length === 0) {
        this.http.get<any>(base, this.authHeader()).subscribe({
          next: (res) => {
            this.coupons = this.extractData(res);
            // Retry with now-populated list
            const couponRetry = this.coupons.find(c => c.couponId == id);
            if (couponRetry) {
              this.newCoupon = {
                couponCode: couponRetry.couponCode,
                discountAmount: couponRetry.discountAmount,
                expiryDate: couponRetry.expiryDate?.substring(0, 10) || ''
              };
              this.showCouponIdPopup = false;
              this.showCouponFormPopup = true;
            } else {
              this.showToastMessage('Coupon not found', 'error');
            }
          },
          error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
        });
      } else {
        this.showToastMessage('Coupon not found', 'error');
      }
      return;
    }

    // Coupon found in list – pre‑fill the form
    this.newCoupon = {
      couponCode: coupon.couponCode,
      discountAmount: coupon.discountAmount,
      expiryDate: coupon.expiryDate?.substring(0, 10) || ''
    };
    this.showCouponIdPopup = false;
    this.showCouponFormPopup = true;
  }
}

  submitValidateCoupon() {
    const code = this.safe(this.validateCouponCode);
    if (!code) {
      this.showToastMessage('Coupon code is required', 'error');
      return;
    }
    const url = `${this.baseUrl}/coupons/${code}`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.singleCoupon = this.extractData(res)[0];
        this.showValidateCouponPopup = false;
        this.showCouponDetailPopup = true;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  submitCouponForm() {
  const id = this.safe(this.couponActionId);
  const base = `${this.baseUrl}/coupons`;

  // ✅ Append T00:00:00 to expiry date so it becomes a valid ISO date-time
  const payload = {
    couponCode: this.newCoupon.couponCode,
    discountAmount: this.newCoupon.discountAmount,
    expiryDate: this.newCoupon.expiryDate ? this.newCoupon.expiryDate + 'T00:00:00' : null
  };

  if (this.couponActionType === 'POST') {
    this.http.post(base, payload, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Coupon Created ✅', 'success');
        this.closeCouponPopups();
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
    return;
  }

  if (this.couponActionType === 'PUT') {
    this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Coupon Updated ✅', 'success');
        this.closeCouponPopups();
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }
}

  resetCouponForm() {
    this.newCoupon = { couponCode: '', discountAmount: 0, expiryDate: '' };
  }

  closeCouponPopups() {
    this.showCouponFormPopup = false;
    this.showCouponIdPopup = false;
    this.showCouponDetailPopup = false;
    this.showValidateCouponPopup = false;
    this.couponActionId = '';
    this.resetCouponForm();
  }

  // ─── Order-Coupon handlers ────────────────────────────
  handleOrderCoupon(ep: any) {
    // Order ID should already be set in contextOrderId
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
  // Ensure we have the full coupon list to map with details
  const loadApplied = () => {
    const url = `${this.baseUrl}/orders/${this.contextOrderId}/coupons`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        const rawArray = this.extractData(res);   // [{ orderId, couponId }, ...]
        this.appliedCoupons = rawArray.map((item: any) => {
          const fullCoupon = this.coupons.find(c => c.couponId == item.couponId);
          return {
            couponId: item.couponId,
            couponCode: fullCoupon?.couponCode || 'Unknown',
            discountAmount: fullCoupon?.discountAmount || 0,
            expiryDate: fullCoupon?.expiryDate || ''
          };
        });
        this.showAppliedCouponsPopup = true;
        this.showToastMessage('Applied coupons loaded', 'success');
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  };

  // If coupons list is empty, fetch it first, then load applied
  if (this.coupons.length === 0) {
    this.http.get<any>(`${this.baseUrl}/coupons`, this.authHeader()).subscribe({
      next: (res) => {
        this.coupons = this.extractData(res);
        loadApplied();
      },
      error: (err) => {
        // If we can't load all coupons, still try to show something (with unknowns)
        loadApplied();
      }
    });
  } else {
    loadApplied();
  }
}
  }

  submitApplyCoupon() {
    const oid = this.safe(this.contextOrderId);
    const cid = this.safe(this.contextCouponIdForApply);
    if (!oid || !cid) {
      this.showToastMessage('Order ID and Coupon ID are required', 'error');
      return;
    }
    const url = `${this.baseUrl}/orders/${oid}/coupons/${cid}`;
    this.http.post(url, {}, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Coupon applied to order ✅', 'success');
        this.showApplyCouponPopup = false;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  submitRemoveCoupon() {
    const oid = this.safe(this.contextOrderId);
    const cid = this.safe(this.contextCouponIdForApply);
    if (!oid || !cid) {
      this.showToastMessage('Order ID and Coupon ID are required', 'error');
      return;
    }
    const url = `${this.baseUrl}/orders/${oid}/coupons/${cid}`;
    this.http.delete(url, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Coupon removed from order ✅', 'success');
        this.showRemoveCouponPopup = false;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  // ─── Rating handlers ──────────────────────────────────
  handleRating(ep: any) {
    if (ep.method === 'POST') {
      // contextOrderId already set from popup
      if (!this.contextOrderId) {
        this.showToastMessage('Order ID is required', 'error');
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

    if (ep.method === 'DELETE') {
      this.ratingActionType = 'DELETE';
      this.ratingActionTitle = 'Remove Rating – Enter ID';
      this.ratingActionId = '';
      this.showRatingIdPopup = true;
      return;
    }
  }

  confirmRatingIdAction() {
    const id = this.safe(this.ratingActionId);
    const base = `${this.baseUrl}/ratings`;

    if (!id) {
      this.showToastMessage('Rating ID is required', 'error');
      return;
    }

    if (this.ratingActionType === 'DELETE') {
  // Use responseType: 'text' because backend may return plain text / empty body
  this.http.delete(`${base}/${id}`, { ...this.authHeader(), responseType: 'text' }).subscribe({
    next: () => {
      this.showToastMessage('Rating removed ✅', 'success');
      this.showRatingIdPopup = false;
    },
    error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
  });
}
  }

  submitRatingForm() {
    const oid = this.safe(this.contextOrderId);
    if (!oid) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }

    // Fetch order to get restaurantId
    const orderUrl = `${this.baseUrl}/orders/${oid}`;
    this.http.get<any>(orderUrl, this.authHeader()).subscribe({
      next: (orderRes) => {
        const order = this.extractData(orderRes)[0];
        const restaurantId = order.restaurantId;

        const ratingUrl = `${this.baseUrl}/orders/${oid}/ratings`;
        const payload = {
          orderId: parseInt(oid),
          restaurantId: restaurantId,
          rating: this.newRating.rating,
          comment: this.newRating.comment
        };

        this.http.post(ratingUrl, payload, this.authHeader()).subscribe({
          next: () => {
            this.showToastMessage('Rating submitted ✅', 'success');
            this.showRatingFormPopup = false;
          },
          error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
        });
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  fetchRestaurantRatings() {
  const rid = this.safe(this.contextRestaurantIdForRatings);
  if (!rid) {
    this.showToastMessage('Restaurant ID is required', 'error');
    return;
  }
  const url = `${this.baseUrl}/restaurants/${rid}/ratings`;
  this.http.get<any>(url, this.authHeader()).subscribe({
    next: (res) => {
      console.log('🔍 Raw restaurant ratings:', JSON.stringify(res, null, 2)); // temporary debug
      const rawArray = this.extractData(res);

      this.ratings = rawArray.map((item: any) => {
        // Comment may be nested under 'rating', 'review', or flat
        const comment = item.comment || item.review || item.text || (item.rating && item.rating.comment) || '';
        return {
          ratingId: item.ratingId || item.id,
          orderId: item.orderId || item.order?.orderId,
          rating: item.rating || item.score,
          comment: comment
        };
      });

      this.showRestaurantIdForRatingsPopup = false;
      this.showRatingsListPopup = true;
      this.showToastMessage('Ratings loaded', 'success');
    },
    error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
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