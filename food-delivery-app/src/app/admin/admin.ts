import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './admin.html',
  styleUrls: ['./admin.css']
})
export class Admin {

  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Admin';
  role = 'All Endpoints Tester';

  // ─── Auth (admin) ──────────────────────────────────────
  private defaultAuth = btoa('admin:admin123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'admin' && storedHeader) {
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

  // ─── Endpoints grouped by member ────────────────────────
  annieCustomerEndpoints = [
    { method: 'POST', path: '/customers/add', desc: 'Create a new customer' },
    { method: 'GET', path: '/customers/getall', desc: 'Fetch all customers' },
    { method: 'GET', path: '/customers/{customerId}', desc: 'Fetch customer details by ID' },
    { method: 'PUT', path: '/customers/{customerId}', desc: 'Update customer profile' },
    { method: 'DELETE', path: '/customers/{customerId}', desc: 'Deactivate/delete customer' },
  ];

  annieAddressEndpoints = [
    { method: 'POST', path: '/customers/{customerId}/addresses', desc: 'Add a delivery address for customer' },
    { method: 'GET', path: '/customers/{customerId}/addresses', desc: 'List all delivery addresses for a customer' },
    { method: 'GET', path: '/addresses/{addressId}', desc: 'Fetch a specific address' },
    { method: 'PUT', path: '/addresses/{addressId}', desc: 'Update address' },
    { method: 'DELETE', path: '/addresses/{addressId}', desc: 'Remove address' },
  ];

  hemanthRestaurantEndpoints = [
    { method: 'GET', path: '/restaurants', desc: 'List restaurants' },
    { method: 'POST', path: '/restaurants', desc: 'Add restaurant' },
    { method: 'GET', path: '/restaurants/{restaurantId}', desc: 'View restaurant details' },
    { method: 'PUT', path: '/restaurants/{restaurantId}', desc: 'Update restaurant' },
    { method: 'DELETE', path: '/restaurants/{restaurantId}', desc: 'Delete restaurant' },
  ];

  hemanthMenuEndpoints = [
    { method: 'POST', path: '/menu-items', desc: 'Add menu item (restaurantId in body)' },
    { method: 'GET', path: '/menu-items/restaurants/{restaurantId}/menu-items', desc: 'View restaurant menu' },
    { method: 'GET', path: '/menu-items/{itemId}', desc: 'View menu item details' },
    { method: 'PUT', path: '/menu-items/{itemId}', desc: 'Update menu item' },
    { method: 'DELETE', path: '/menu-items/{itemId}', desc: 'Remove menu item' },
  ];

  thenmozhiOrderEndpoints = [
    { method: 'POST', path: '/orders', desc: 'Place a new order' },
    { method: 'GET', path: '/orders', desc: 'List all orders (admin view)' },
    { method: 'GET', path: '/orders/{orderId}', desc: 'Fetch order details' },
    { method: 'PUT', path: '/orders/{orderId}/status', desc: 'Update order status' },
    { method: 'GET', path: '/customers/{customerId}/orders', desc: 'Fetch all orders of a customer' },
    { method: 'GET', path: '/restaurants/{restaurantId}/orders', desc: 'Fetch orders for a restaurant' },
  ];

  thenmozhiOrderItemEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/items', desc: 'Add item to order' },
    { method: 'GET', path: '/orders/{orderId}/items', desc: 'Get all items in an order' },
    { method: 'PUT', path: '/order-items/{orderItemId}', desc: 'Update quantity' },
    { method: 'DELETE', path: '/order-items/{orderItemId}', desc: 'Remove item from order' },
  ];

  kisolDriverEndpoints = [
    { method: 'POST', path: '/drivers', desc: 'Register delivery driver' },
    { method: 'GET', path: '/drivers', desc: 'List all drivers' },
    { method: 'GET', path: '/drivers/{driverId}', desc: 'View driver profile' },
    { method: 'PUT', path: '/drivers/{driverId}', desc: 'Update driver information' },
    { method: 'DELETE', path: '/drivers/{driverId}', desc: 'Deactivate driver' },
  ];

  kisolAssignmentEndpoints = [
    { method: 'PUT', path: '/orders/{orderId}/assign-driver/{driverId}', desc: 'Assign driver to order' },
    { method: 'GET', path: '/drivers/{driverId}/orders', desc: 'List orders assigned to driver' },
    { method: 'PUT', path: '/orders/{orderId}/delivery-status', desc: 'Update delivery status' },
  ];

  jeevithaCouponEndpoints = [
    { method: 'POST', path: '/coupons', desc: 'Create coupon' },
    { method: 'GET', path: '/coupons', desc: 'List all coupons' },
    { method: 'GET', path: '/coupons/{couponCode}', desc: 'Validate coupon' },
    { method: 'PUT', path: '/coupons/{couponId}', desc: 'Update coupon' },
    { method: 'DELETE', path: '/coupons/{couponId}', desc: 'Disable coupon' },
  ];

  jeevithaOrderCouponEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/coupons/{couponId}', desc: 'Apply coupon to order' },
    { method: 'DELETE', path: '/orders/{orderId}/coupons/{couponId}', desc: 'Remove applied coupon' },
    { method: 'GET', path: '/orders/{orderId}/coupons', desc: 'View coupons applied to an order' },
  ];

  jeevithaRatingEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/ratings', desc: 'Submit rating for restaurant' },
    { method: 'GET', path: '/restaurants/{restaurantId}/ratings', desc: 'View all ratings for restaurant' },
    { method: 'DELETE', path: '/ratings/{ratingId}', desc: 'Remove rating (admin/moderation)' },
  ];

  // ─── Toast notification ───────────────────────────────
  toastMessage = '';
  toastType: 'success' | 'error' = 'success';
  showToast = false;

  // ─── Context popup for IDs ────────────────────────────
  showContextPopup = false;
  contextTitle = '';
  contextPlaceholder = '';
  contextInputValue = '';
  // Callback function that will be called with the entered ID
  contextCallback: ((id: string) => void) | null = null;

  // ─── Payload popup for POST/PUT with body ─────────────
  showPayloadPopup = false;
  payloadTitle = '';
  payloadJson = '{}';
  payloadCallback: ((json: any) => void) | null = null;

  // ─── Helpers ──────────────────────────────────────────
  private showToastMessage(msg: string, type: 'success' | 'error' = 'success') {
    this.toastMessage = msg;
    this.toastType = type;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 4000);
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

  // ─── Context & Payload flow ────────────────────────────
  // Open a popup to ask for a single ID
  askForId(title: string, placeholder: string, callback: (id: string) => void) {
    this.contextTitle = title;
    this.contextPlaceholder = placeholder;
    this.contextInputValue = '';
    this.contextCallback = callback;
    this.showContextPopup = true;
  }

  submitContext() {
    const id = this.safe(this.contextInputValue);
    if (!id) {
      this.showToastMessage('ID is required', 'error');
      return;
    }
    this.showContextPopup = false;
    if (this.contextCallback) {
      this.contextCallback(id);
    }
  }

  // Open a popup to enter JSON payload
  askForPayload(title: string, callback: (json: any) => void, prefilledJson: any = {}) {
    this.payloadTitle = title;
    this.payloadJson = JSON.stringify(prefilledJson, null, 2);
    this.payloadCallback = callback;
    this.showPayloadPopup = true;
  }

  submitPayload() {
    try {
      const parsed = JSON.parse(this.payloadJson);
      this.showPayloadPopup = false;
      if (this.payloadCallback) {
        this.payloadCallback(parsed);
      }
    } catch (e) {
      this.showToastMessage('Invalid JSON', 'error');
    }
  }

  // ─── HTTP wrapper ─────────────────────────────────────
  private execute(method: string, url: string, body: any = null, onSuccess: (data: any) => void = () => {}) {
    const headers = this.authHeader();
    let request$;
    if (method === 'GET') {
      request$ = this.http.get<any>(url, headers);
    } else if (method === 'POST') {
      request$ = this.http.post<any>(url, body, headers);
    } else if (method === 'PUT') {
      request$ = this.http.put<any>(url, body, headers);
    } else if (method === 'DELETE') {
      request$ = this.http.delete<any>(url, headers);
    } else {
      return;
    }
    request$.subscribe({
      next: (res) => {
        this.showToastMessage('Success ✅', 'success');
        onSuccess(res);
      },
      error: (err) => {
        this.showToastMessage(err?.error?.message ?? err?.message ?? 'Error', 'error');
      }
    });
  }

  // ─── Route handlers per member/module ─────────────────

  // Annie
  runAnnieCustomer(ep: any) {
    const base = `${this.baseUrl}/customers`;
    if (ep.method === 'POST') {
      this.askForPayload('Create Customer', (json) => {
        this.execute('POST', `${base}/add`, json);
      }, { customerName: '', customerEmail: '', customerPhone: '' });
    } else if (ep.path === '/customers/getall') {
      this.execute('GET', `${base}/getall`);
    } else if (ep.path.includes('{customerId}')) {
      this.askForId('Customer ID', 'Customer ID', (id) => {
        const url = `${base}/${id}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          // For PUT, first fetch existing, then show prefilled payload
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Customer', (json) => {
              this.execute('PUT', url, json);
            }, data);
          });
        }
        else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  runAnnieAddress(ep: any) {
    if (ep.path.includes('{customerId}')) {
      this.askForId('Customer ID for Address operation', 'Customer ID', (cid) => {
        if (ep.method === 'POST') {
          this.askForPayload('Add Address', (json) => {
            this.execute('POST', `${this.baseUrl}/customers/${cid}/addresses`, json);
          }, { addressLine1: '', addressLine2: '', city: '', state: '', postalCode: '', country: '', customerId: cid });
        } else if (ep.method === 'GET') {
          this.execute('GET', `${this.baseUrl}/customers/${cid}/addresses`);
        }
      });
    } else {
      // address by id
      this.askForId('Address ID', 'Address ID', (addrId) => {
        const url = `${this.baseUrl}/addresses/${addrId}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Address', (json) => {
              this.execute('PUT', url, json);
            }, data);
          });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  // Hemanth
  runHemanthRestaurant(ep: any) {
    const base = `${this.baseUrl}/restaurants`;
    if (ep.method === 'POST') {
      this.askForPayload('Add Restaurant', (json) => this.execute('POST', base, json), { restaurantName: '', restaurantAddress: '', restaurantPhone: '' });
    } else if (ep.path.includes('{restaurantId}')) {
      this.askForId('Restaurant ID', 'Restaurant ID', (id) => {
        const url = `${base}/${id}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Restaurant', (json) => this.execute('PUT', url, json), data);
          });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  runHemanthMenu(ep: any) {
    const menuBase = `${this.baseUrl}/menu-items`;
    if (ep.path === '/menu-items') {
      // POST (need restaurantId in body)
      this.askForId('Restaurant ID for Menu Item', 'Restaurant ID', (rid) => {
        this.askForPayload('Add Menu Item', (json) => {
          json.restaurantId = rid;
          this.execute('POST', menuBase, json);
        }, { itemName: '', itemDescription: '', itemPrice: 0 });
      });
    } else if (ep.path.includes('restaurants/{restaurantId}')) {
      this.askForId('Restaurant ID', 'Restaurant ID', (rid) => {
        this.execute('GET', `${menuBase}/restaurants/${rid}/menu-items`);
      });
    } else {
      // itemId based
      this.askForId('Menu Item ID', 'Menu Item ID', (id) => {
        const url = `${menuBase}/${id}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Menu Item', (json) => {
              json.restaurantId = data.restaurantId; // keep same restaurant
              this.execute('PUT', url, json);
            }, data);
          });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  // Thenmozhi
  runThenmozhiOrder(ep: any) {
    const base = `${this.baseUrl}/orders`;
    if (ep.method === 'POST') {
      this.askForPayload('Place Order', (json) => {
        json.orderDate = new Date().toISOString();
        json.orderStatus = 'PENDING';
        this.execute('POST', base, json);
      }, { customerId: '', restaurantId: '' });
    } else if (ep.path === '/orders') {
      this.execute('GET', base);
    } else if (ep.path.includes('{orderId}')) {
      this.askForId('Order ID', 'Order ID', (id) => {
        const url = `${base}/${id}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          this.askForPayload('Update Order Status', (json) => {
            this.execute('PUT', `${base}/${id}/status`, { status: json.status });
          }, { status: 'CONFIRMED' });
        }
      });
    } else if (ep.path.includes('/customers/')) {
      this.askForId('Customer ID', 'Customer ID', (cid) => this.execute('GET', `${this.baseUrl}/customers/${cid}/orders`));
    } else if (ep.path.includes('/restaurants/')) {
      this.askForId('Restaurant ID', 'Restaurant ID', (rid) => this.execute('GET', `${this.baseUrl}/restaurants/${rid}/orders`));
    }
  }

  runThenmozhiOrderItem(ep: any) {
    if (ep.path.includes('{orderId}')) {
      this.askForId('Order ID for items', 'Order ID', (oid) => {
        if (ep.method === 'POST') {
          this.askForPayload('Add Item to Order', (json) => {
            this.execute('POST', `${this.baseUrl}/orders/${oid}/items`, json);
          }, { itemId: '', quantity: 1 });
        } else if (ep.method === 'GET') {
          this.execute('GET', `${this.baseUrl}/orders/${oid}/items`);
        }
      });
    } else {
      // order-item id endpoints
      this.askForId('Order Item ID', 'Order Item ID', (id) => {
        const url = `${this.baseUrl}/order-items/${id}`;
        if (ep.method === 'PUT') {
          this.askForPayload('Update Quantity', (json) => {
            this.execute('PUT', url, json);
          }, { quantity: 1 });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  // Kisol
  runKisolDriver(ep: any) {
    const base = `${this.baseUrl}/drivers`;
    if (ep.method === 'POST') {
      this.askForPayload('Register Driver', (json) => this.execute('POST', base, json), { driverName: '', driverPhone: '', driverVehicle: '' });
    } else if (ep.path.includes('{driverId}')) {
      this.askForId('Driver ID', 'Driver ID', (id) => {
        const url = `${base}/${id}`;
        if (ep.method === 'GET') this.execute('GET', url);
        else if (ep.method === 'PUT') {
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Driver', (json) => this.execute('PUT', url, json), data);
          });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  runKisolAssignment(ep: any) {
    if (ep.path.includes('assign-driver')) {
      this.askForId('Order ID', 'Order ID', (oid) => {
        this.askForId('Driver ID', 'Driver ID', (did) => {
          this.execute('PUT', `${this.baseUrl}/orders/${oid}/assign-driver/${did}`, {});
        });
      });
    } else if (ep.path.includes('delivery-status')) {
      this.askForId('Order ID', 'Order ID', (oid) => {
        this.askForPayload('Delivery Status', (json) => {
          this.execute('PUT', `${this.baseUrl}/orders/${oid}/delivery-status`, json);
        }, { status: 'OUT_FOR_DELIVERY' });
      });
    } else if (ep.path.includes('/drivers/{driverId}/orders')) {
      this.askForId('Driver ID', 'Driver ID', (did) => {
        this.execute('GET', `${this.baseUrl}/drivers/${did}/orders`);
      });
    }
  }

  // Jeevitha
  runJeevithaCoupon(ep: any) {
    const base = `${this.baseUrl}/coupons`;
    if (ep.method === 'POST') {
      this.askForPayload('Create Coupon', (json) => this.execute('POST', base, json), { couponCode: '', discountAmount: 0, expiryDate: '' });
    } else if (ep.path.includes('{couponCode}')) {
      // validate coupon by code
      this.askForId('Coupon Code', 'Coupon Code', (code) => this.execute('GET', `${base}/${code}`));
    } else if (ep.path.includes('{couponId}')) {
      this.askForId('Coupon ID', 'Coupon ID (numeric)', (id) => {
        const url = `${base}/${id}`;
        if (ep.method === 'PUT') {
          this.execute('GET', url, null, (res) => {
            const data = this.extractData(res)[0];
            this.askForPayload('Update Coupon', (json) => this.execute('PUT', url, json), data);
          });
        } else if (ep.method === 'DELETE') this.execute('DELETE', url);
      });
    }
  }

  runJeevithaOrderCoupon(ep: any) {
    this.askForId('Order ID for coupon', 'Order ID', (oid) => {
      if (ep.method === 'GET') {
        this.execute('GET', `${this.baseUrl}/orders/${oid}/coupons`);
      } else if (ep.method === 'POST' || ep.method === 'DELETE') {
        this.askForId('Coupon ID', 'Coupon ID (numeric)', (cid) => {
          const url = `${this.baseUrl}/orders/${oid}/coupons/${cid}`;
          if (ep.method === 'POST') this.execute('POST', url, {});
          else if (ep.method === 'DELETE') this.execute('DELETE', url);
        });
      }
    });
  }

  runJeevithaRating(ep: any) {
    if (ep.method === 'POST') {
      this.askForId('Order ID for rating', 'Order ID', (oid) => {
        // Fetch order to get restaurantId
        this.execute('GET', `${this.baseUrl}/orders/${oid}`, null, (orderRes) => {
          const order = this.extractData(orderRes)[0];
          const restaurantId = order.restaurantId;
          this.askForPayload('Submit Rating', (json) => {
            json.orderId = oid;
            json.restaurantId = restaurantId;
            this.execute('POST', `${this.baseUrl}/orders/${oid}/ratings`, json);
          }, { rating: 5, comment: '' });
        });
      });
    } else if (ep.path.includes('/restaurants/')) {
      this.askForId('Restaurant ID', 'Restaurant ID', (rid) => {
        this.execute('GET', `${this.baseUrl}/restaurants/${rid}/ratings`);
      });
    } else if (ep.path.includes('/ratings/{ratingId}')) {
      this.askForId('Rating ID', 'Rating ID', (id) => {
        this.execute('DELETE', `${this.baseUrl}/ratings/${id}`);
      });
    }
  }

}