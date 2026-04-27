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

  // Auth (matches backend user "jeevitha") 
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

  // Helper: Extract clean error message
  private extractErrorMessage(err: any): string {
    // Case 1: Backend returns message with "default message [xxx]" pattern
    if (err?.error?.message && typeof err.error.message === 'string') {
      let msg = err.error.message;
      // Extract text between "default message [" and "]"
      const match = msg.match(/default message \[(.*?)\]/);
      if (match && match[1]) {
        return match[1];  // Returns clean message like "Discount amount must be greater than 0"
      }
      // If message is short and clean, return it directly
      if (msg.length < 200 && !msg.includes('Validation failed')) {
        return msg;
      }
    }
    
    // Case 2: Simple message from backend
    if (err?.error?.message && typeof err.error.message === 'string' && err.error.message.length < 200) {
      return err.error.message;
    }
    
    // Case 3: Validation errors array
    if (err?.error?.errors && err.error.errors.length > 0) {
      const firstError = err.error.errors[0];
      if (firstError.defaultMessage) {
        return firstError.defaultMessage;
      }
      if (firstError.message) {
        return firstError.message;
      }
    }
    
    // Case 4: Simple error message
    if (err?.message && err.message.length < 200) {
      return err.message;
    }
    
    // Case 5: Specific check for Positive validation
    if (err?.error?.message && err.error.message.includes('Positive')) {
      return 'Discount amount must be greater than 0';
    }
    
    // Case 6: Specific check for NotBlank validation
    if (err?.error?.message && err.error.message.includes('NotBlank')) {
      return 'Coupon code cannot be empty';
    }
    
    // Case 7: Specific check for Future validation
    if (err?.error?.message && err.error.message.includes('Future')) {
      return 'Expiry date must be in the future';
    }
    
    return 'Validation failed';
  }

  // Endpoints 
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

  // State variables
  coupons: any[] = [];
  appliedCoupons: any[] = [];
  ratings: any[] = [];
  singleCoupon: any = null;
  singleRating: any = null;

  couponActionId = '';
  couponActionType = '';
  couponActionTitle = '';
  validateCouponCode = '';

  contextOrderId = '';
  contextCouponIdForApply = '';

  contextRestaurantIdForRatings = '';
  ratingActionId = '';
  ratingActionType = '';
  ratingActionTitle = '';

  //Form models
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

  showOrderContextPopup = false;
  pendingOrderCouponEndpoint: any = null;
  pendingRatingEndpoint: any = null;

  //Toast notification
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  private showToastMessage(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 4000);
  }

  // Helpers 
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

  //Routes endpoint to appropriate handler
  handleEndpoint(ep: any) {
    if (ep.path.startsWith('/coupons')) {
      this.handleCoupon(ep);
      return;
    }

    if (ep.path.includes('/orders') && ep.path.includes('coupons')) {
      this.pendingOrderCouponEndpoint = ep;
      this.pendingRatingEndpoint = null;
      this.contextOrderId = '';
      this.showOrderContextPopup = true;
      return;
    }

    if (ep.path.includes('/orders/{orderId}/ratings')) {
      this.pendingRatingEndpoint = ep;
      this.pendingOrderCouponEndpoint = null;
      this.contextOrderId = '';
      this.showOrderContextPopup = true;
      return;
    }

    this.handleRating(ep);
  }

  //called when submits OrderId in popup
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

  // Coupon handlers 
  handleCoupon(ep: any) {
    const base = `${this.baseUrl}/coupons`;

    //create coupon 
    if (ep.method === 'POST') {
      this.resetCouponForm();
      this.couponActionType = 'POST';
      this.showCouponFormPopup = true;
      return;
    }
//display all coupons
    if (ep.method === 'GET' && ep.path === '/coupons') {
      this.http.get<any>(base, this.authHeader()).subscribe({
        next: (res) => {
          this.coupons = this.extractData(res);
          this.showCouponsListPopup = true;
          this.showToastMessage('Coupons loaded', 'success');
        },
        error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
      });
      return;
    }

    if (ep.method === 'GET' && ep.path === '/coupons/{couponCode}') {
      this.validateCouponCode = '';
      this.showValidateCouponPopup = true;
      return;
    }
//ask for coupon ID first -update
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

  //delete coupon
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
        error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
      });
      return;
    }

    //find coupon and show update form
    if (this.couponActionType === 'PUT') {
      const coupon = this.coupons.find(c => c.couponId == id);
      if (!coupon) {
        if (this.coupons.length === 0) {
          this.http.get<any>(base, this.authHeader()).subscribe({
            next: (res) => {
              this.coupons = this.extractData(res);
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
            error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
          });
        } else {
          this.showToastMessage('Coupon not found', 'error');
        }
        return;
      }

      this.newCoupon = {
        couponCode: coupon.couponCode,
        discountAmount: coupon.discountAmount,
        expiryDate: coupon.expiryDate?.substring(0, 10) || ''
      };
      this.showCouponIdPopup = false;
      this.showCouponFormPopup = true;
    }
  }

  //validate coupon by code
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
      error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
    });
  }

  //submit create/update coupon form
  submitCouponForm() {
    const id = this.safe(this.couponActionId);
    const base = `${this.baseUrl}/coupons`;

    const payload = {
      couponCode: this.newCoupon.couponCode,
      discountAmount: this.newCoupon.discountAmount,
      expiryDate: this.newCoupon.expiryDate ? this.newCoupon.expiryDate + 'T00:00:00' : null
    };

    //create coupon
    if (this.couponActionType === 'POST') {
      this.http.post(base, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Coupon Created ✅', 'success');
          this.closeCouponPopups();
        },
        error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
      });
      return;
    }

    //update coupon
    if (this.couponActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Coupon Updated ✅', 'success');
          this.closeCouponPopups();
        },
        error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
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

  // Order-Coupon handlers
  handleOrderCoupon(ep: any) {
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
      const loadApplied = () => {
        const url = `${this.baseUrl}/orders/${this.contextOrderId}/coupons`;
        this.http.get<any>(url, this.authHeader()).subscribe({
          next: (res) => {
            const rawArray = this.extractData(res);
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
          error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
        });
      };

      if (this.coupons.length === 0) {
        this.http.get<any>(`${this.baseUrl}/coupons`, this.authHeader()).subscribe({
          next: (res) => {
            this.coupons = this.extractData(res);
            loadApplied();
          },
          error: (err) => {
            loadApplied();
          }
        });
      } else {
        loadApplied();
      }
    }
  }


//submit apply coupon request
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
      error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
    });
  }

  //submit remove coupon request
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
      error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
    });
  }

  // Rating handlers
  handleRating(ep: any) {
    if (ep.method === 'POST') {
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

  //confirm rating ID action
  confirmRatingIdAction() {
    const id = this.safe(this.ratingActionId);
    const base = `${this.baseUrl}/ratings`;

    if (!id) {
      this.showToastMessage('Rating ID is required', 'error');
      return;
    }

    if (this.ratingActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, { ...this.authHeader(), responseType: 'text' }).subscribe({
        next: () => {
          this.showToastMessage('Rating removed ✅', 'success');
          this.showRatingIdPopup = false;
        },
        error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
      });
    }
  }

  submitRatingForm() {
    const oid = this.safe(this.contextOrderId);
    if (!oid) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }

    //first fetch order to get restaurantID
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
          error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
        });
      },
      error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
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
        const rawArray = this.extractData(res);
        this.ratings = rawArray.map((item: any) => {
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
      error: (err) => this.showToastMessage(this.extractErrorMessage(err), 'error')
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