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
  name = 'Thenmozhi';
  role = 'Order & Order Item API Tester';

  // ================= ENDPOINTS DISPLAY =================
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

  // ================= STATE =================
  orders: any[] = [];
  orderItems: any[] = [];
  singleOrder: any = null;
  singleOrderItem: any = null;

  orderActionId = '';
  orderActionType = '';
  orderActionTitle = '';

  contextCustomerId = '';
  contextRestaurantId = '';
  contextOrderId = '';

  orderItemActionId = '';
  orderItemActionType = '';
  orderItemActionTitle = '';

  // ✅ No driver field for customer
  newOrder = {
    customerId: '',
    restaurantId: ''
  };

  newOrderItem = {
    itemId: '',
    quantity: 1
  };

  // ✅ Status dropdown – DELIVERED is set by driver, not here
  orderStatusUpdate = {
    status: 'CONFIRMED'
  };

  // ================= POPUP FLAGS =================
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

  // ================= ORDER HANDLERS =================
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
        },
        error: (err) => this.showError(err)
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
      alert('Order ID is required');
      return;
    }

    if (this.orderActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleOrder = this.extractData(res)[0];
          this.showOrderIdPopup = false;
          this.showOrderDetailPopup = true;
        },
        error: (err) => this.showError(err)
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
        alert('Order status updated ✅');
        this.showOrderStatusPopup = false;
        this.orderActionId = '';
      },
      error: (err) => this.showError(err)
    });
  }

  submitOrderForm() {
    const base = `${this.baseUrl}/orders`;
    // ✅ Send date and status to pass validation; the service will ignore/override them
    const payload = {
      customerId: parseInt(this.newOrder.customerId, 10),
      restaurantId: parseInt(this.newOrder.restaurantId, 10),
      orderDate: new Date().toISOString(),
      orderStatus: 'PENDING'
    };

    this.http.post(base, payload, this.authHeader()).subscribe({
      next: () => {
        alert('Order Placed ✅');
        this.closeOrderPopups();
      },
      error: (err) => this.showError(err)
    });
  }

  fetchCustomerOrders() {
    const cid = this.safe(this.contextCustomerId);
    if (!cid) {
      alert('Customer ID is required');
      return;
    }
    const url = `${this.baseUrl}/customers/${cid}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.orders = this.extractData(res);
        this.showCustomerIdInputPopup = false;
        this.showOrdersListPopup = true;
      },
      error: (err) => this.showError(err)
    });
  }

  fetchRestaurantOrders() {
    const rid = this.safe(this.contextRestaurantId);
    if (!rid) {
      alert('Restaurant ID is required');
      return;
    }
    const url = `${this.baseUrl}/restaurants/${rid}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.orders = this.extractData(res);
        this.showRestaurantIdInputPopup = false;
        this.showOrdersListPopup = true;
      },
      error: (err) => this.showError(err)
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

  // ================= ORDER ITEM HANDLERS =================
  handleOrderItem(ep: any) {
    const oid = this.safe(this.contextOrderId);
    if (!oid) {
      alert('Please enter an Order ID (context) first');
      return;
    }

    // POST /orders/{orderId}/items
    if (ep.method === 'POST' && ep.path.includes('/orders/')) {
      this.resetOrderItemForm();
      this.orderItemActionType = 'POST';
      this.showOrderItemFormPopup = true;
      return;
    }

    // GET /orders/{orderId}/items
    if (ep.method === 'GET' && ep.path.includes('/orders/')) {
      const url = `${this.baseUrl}/orders/${oid}/items`;
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.orderItems = this.extractData(res);
          this.showOrderItemsListPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // PUT /order-items/{orderItemId}
    if (ep.method === 'PUT') {
      this.orderItemActionType = 'PUT';
      this.orderItemActionTitle = 'Update Quantity – Enter Order Item ID';
      this.orderItemActionId = '';
      this.showOrderItemIdPopup = true;
      return;
    }

    // DELETE /order-items/{orderItemId}
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
      alert('Order Item ID is required');
      return;
    }

    if (this.orderItemActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert('Order Item Removed ✅');
          this.showOrderItemIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.orderItemActionType === 'PUT') {
      // Fetch existing item to pre-fill quantity
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
        error: (err) => this.showError(err)
      });
    }
  }

  submitOrderItemForm() {
    const oid = this.safe(this.contextOrderId);
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
          alert('Item added to order ✅');
          this.closeOrderItemPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.orderItemActionType === 'PUT') {
      // Send ONLY quantity, not the whole payload
      this.http.put(`${base}/${id}`, { quantity: payload.quantity }, this.authHeader()).subscribe({
        next: () => {
          alert('Quantity updated ✅');
          this.closeOrderItemPopups();
        },
        error: (err) => this.showError(err)
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