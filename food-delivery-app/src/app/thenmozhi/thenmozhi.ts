import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-thenmozhi',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './thenmozhi.html',
  styleUrls: ['./thenmozhi.css']
})
export class Thenmozhi {

  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Thenmozhi S';
  role = 'Order & Order Item API Tester';

  // ─── Auth (matches backend user "thenmozli") ─────────
  private defaultAuth = btoa('admin:admin123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'thenmozli' && storedHeader) {
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
  orderEndpoints = [
    { method: 'POST', path: '/orders', desc: 'Place a new order' },
    { method: 'GET', path: '/orders', desc: 'List all orders (admin view)' },
    { method: 'GET', path: '/orders/{orderId}', desc: 'Fetch order details' },
    { method: 'PUT', path: '/orders/{orderId}/status', desc: 'Update order status (Pending → Confirmed → Out for Delivery / Cancelled)' },
    { method: 'GET', path: '/customers/{customerId}/orders', desc: 'Fetch all orders of a customer' },
    { method: 'GET', path: '/restaurants/{restaurantId}/orders', desc: 'Fetch orders for a restaurant' },
  ];

  orderItemEndpoints = [
    { method: 'POST', path: '/orders/{orderId}/items', desc: 'Add item to order' },
    { method: 'GET', path: '/orders/{orderId}/items', desc: 'Get all items in an order' },
    { method: 'PUT', path: '/order-items/{orderItemId}', desc: 'Update quantity' },
    { method: 'DELETE', path: '/order-items/{orderItemId}', desc: 'Remove item from order' },
  ];

  // ─── State ────────────────────────────────────────────
  orders: any[] = [];
  orderItems: any[] = [];
  singleOrder: any = null;
  singleOrderItem: any = null;

  orderActionId = '';
  orderActionType = '';
  orderActionTitle = '';

  contextCustomerId = '';
  contextRestaurantId = '';
  // contextOrderId removed, now we use orderContextId from popup
  orderContextId = '';   // used by order item context popup

  orderItemActionId = '';
  orderItemActionType = '';
  orderItemActionTitle = '';

  newOrder = {
    customerId: '',
    restaurantId: ''
  };

  newOrderItem = {
    itemId: '',
    quantity: 1
  };

  orderStatusUpdate = {
    status: 'CONFIRMED'
  };

  // Popup flags
  showOrdersListPopup = false;
  showOrderFormPopup = false;
  showOrderIdPopup = false;
  showOrderDetailPopup = false;
  showOrderStatusPopup = false;

  showCustomerIdInputPopup = false;
  showRestaurantIdInputPopup = false;

  showOrderItemsListPopup = false;
  showOrderItemFormPopup = false;
  showOrderItemIdPopup = false;
  showOrderItemDetailPopup = false;

  showOrderContextPopup = false;        // popup for Order ID when needed by order items
  pendingOrderItemEndpoint: any = null; // store the endpoint that triggered order context

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
    // Order item endpoints that involve an order ID first
    if (ep.path.includes('/orders/{orderId}/items')) {
      this.pendingOrderItemEndpoint = ep;
      this.orderContextId = '';
      this.showOrderContextPopup = true;
      return;
    }

    // Other order item endpoints (PUT/DELETE /order-items/...)
    if (ep.path.startsWith('/order-items')) {
      this.handleOrderItem(ep);
      return;
    }

    // All order endpoints and customer/restaurant order lookups
    this.handleOrder(ep);
  }

  // Called after user enters Order ID for order item operations
  confirmOrderContext() {
    const oid = this.safe(this.orderContextId);
    if (!oid) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }
    this.showOrderContextPopup = false;
    if (this.pendingOrderItemEndpoint) {
      // use the collected order ID for this operation
      this.orderContextId = oid;  // store for later use
      this.handleOrderItem(this.pendingOrderItemEndpoint);
    }
  }

  // ─── Order handlers ──────────────────────────────────
  handleOrder(ep: any) {
    const base = `${this.baseUrl}/orders`;

    if (ep.method === 'POST') {
      this.resetOrderForm();
      this.orderActionType = 'POST';
      this.showOrderFormPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path === '/orders') {
      this.http.get<any>(base, this.authHeader()).subscribe({
        next: (res) => {
          this.orders = this.extractData(res);
          this.showOrdersListPopup = true;
          this.showToastMessage('Orders loaded', 'success');
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (ep.method === 'GET' && ep.path === '/orders/{orderId}') {
      this.orderActionType = 'GET_BY_ID';
      this.orderActionTitle = 'View Order Details – Enter Order ID';
      this.orderActionId = '';
      this.showOrderIdPopup = true;
      return;
    }

    if (ep.method === 'PUT' && ep.path.includes('/status')) {
      this.orderActionType = 'UPDATE_STATUS';
      this.orderActionTitle = 'Update Order Status – Enter Order ID';
      this.orderActionId = '';
      this.orderStatusUpdate.status = 'CONFIRMED';
      this.showOrderIdPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path.includes('/customers/')) {
      this.contextCustomerId = '';
      this.showCustomerIdInputPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path.includes('/restaurants/')) {
      this.contextRestaurantId = '';
      this.showRestaurantIdInputPopup = true;
      return;
    }
  }

  confirmOrderIdAction() {
    const id = this.safe(this.orderActionId);
    const base = `${this.baseUrl}/orders`;

    if (!id) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }

    if (this.orderActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleOrder = this.extractData(res)[0];
          this.showOrderIdPopup = false;
          this.showOrderDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.orderActionType === 'UPDATE_STATUS') {
      this.showOrderIdPopup = false;
      this.showOrderStatusPopup = true;
    }
  }

  submitOrderStatusUpdate() {
    const id = this.safe(this.orderActionId);
    const url = `${this.baseUrl}/orders/${id}/status`;
    const payload = { status: this.orderStatusUpdate.status };
    this.http.put(url, payload, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Order status updated ✅', 'success');
        this.showOrderStatusPopup = false;
        this.orderActionId = '';
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  submitOrderForm() {
    const base = `${this.baseUrl}/orders`;
    const payload = {
      customerId: parseInt(this.newOrder.customerId, 10),
      restaurantId: parseInt(this.newOrder.restaurantId, 10),
      orderDate: new Date().toISOString(),
      orderStatus: 'PENDING'
    };

    this.http.post(base, payload, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Order Placed ✅', 'success');
        this.closeOrderPopups();
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  fetchCustomerOrders() {
    const cid = this.safe(this.contextCustomerId);
    if (!cid) {
      this.showToastMessage('Customer ID is required', 'error');
      return;
    }
    const url = `${this.baseUrl}/customers/${cid}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.orders = this.extractData(res);
        this.showCustomerIdInputPopup = false;
        this.showOrdersListPopup = true;
        this.showToastMessage('Customer orders loaded', 'success');
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  fetchRestaurantOrders() {
    const rid = this.safe(this.contextRestaurantId);
    if (!rid) {
      this.showToastMessage('Restaurant ID is required', 'error');
      return;
    }
    const url = `${this.baseUrl}/restaurants/${rid}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.orders = this.extractData(res);
        this.showRestaurantIdInputPopup = false;
        this.showOrdersListPopup = true;
        this.showToastMessage('Restaurant orders loaded', 'success');
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  resetOrderForm() {
    this.newOrder = { customerId: '', restaurantId: '' };
  }

  closeOrderPopups() {
    this.showOrderFormPopup = false;
    this.showOrderIdPopup = false;
    this.showOrderDetailPopup = false;
    this.showOrderStatusPopup = false;
    this.orderActionId = '';
    this.resetOrderForm();
  }

  // ─── Order Item handlers ─────────────────────────────
  handleOrderItem(ep: any) {
    const oid = this.safe(this.orderContextId);  // will be set if needed

    // For endpoints that require an order ID (POST/GET /orders/{orderId}/items)
    if (ep.path.includes('/orders/{orderId}/items')) {
      if (!oid) {
        // Should not happen because we collected it earlier
        this.showToastMessage('Order ID is missing', 'error');
        return;
      }

      if (ep.method === 'POST') {
        this.resetOrderItemForm();
        this.orderItemActionType = 'POST';
        // orderContextId already contains the order ID
        this.showOrderItemFormPopup = true;
        return;
      }

      if (ep.method === 'GET') {
        const url = `${this.baseUrl}/orders/${oid}/items`;
        this.http.get<any>(url, this.authHeader()).subscribe({
          next: (res) => {
            this.orderItems = this.extractData(res);
            this.showOrderItemsListPopup = true;
            this.showToastMessage('Order items loaded', 'success');
          },
          error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
        });
        return;
      }
    }

    // For endpoints that use Order Item ID (PUT/DELETE /order-items/...)
    if (ep.method === 'PUT') {
      this.orderItemActionType = 'PUT';
      this.orderItemActionTitle = 'Update Quantity – Enter Order Item ID';
      this.orderItemActionId = '';
      this.showOrderItemIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.orderItemActionType = 'DELETE';
      this.orderItemActionTitle = 'Remove Order Item – Enter ID';
      this.orderItemActionId = '';
      this.showOrderItemIdPopup = true;
      return;
    }
  }

  confirmOrderItemIdAction() {
    const id = this.safe(this.orderItemActionId);
    const base = `${this.baseUrl}/order-items`;

    if (!id) {
      this.showToastMessage('Order Item ID is required', 'error');
      return;
    }

    if (this.orderItemActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Order Item Removed ✅', 'success');
          this.showOrderItemIdPopup = false;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.orderItemActionType === 'PUT') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newOrderItem = {
            itemId: data.itemId,
            quantity: data.quantity
          };
          this.showOrderItemIdPopup = false;
          this.showOrderItemFormPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  submitOrderItemForm() {
    const oid = this.safe(this.orderContextId);   // valid for POST
    const id = this.safe(this.orderItemActionId);
    const base = `${this.baseUrl}/order-items`;

    const payload = {
      itemId: parseInt(this.newOrderItem.itemId, 10),
      quantity: this.newOrderItem.quantity
    };

    if (this.orderItemActionType === 'POST') {
      const url = `${this.baseUrl}/orders/${oid}/items`;
      this.http.post(url, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Item added to order ✅', 'success');
          this.closeOrderItemPopups();
          this.orderContextId = '';  // reset
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.orderItemActionType === 'PUT') {
      this.http.put(`${base}/${id}`, { quantity: payload.quantity }, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Quantity updated ✅', 'success');
          this.closeOrderItemPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  resetOrderItemForm() {
    this.newOrderItem = { itemId: '', quantity: 1 };
  }

  closeOrderItemPopups() {
    this.showOrderItemFormPopup = false;
    this.showOrderItemIdPopup = false;
    this.showOrderItemDetailPopup = false;
    this.orderItemActionId = '';
    this.resetOrderItemForm();
  }
}