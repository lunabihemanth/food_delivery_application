import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-kisol',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './kisol.html',
  styleUrls: ['./kisol.css']
})
export class Kisol {

  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Kisol Shamilisha';
  role = 'Driver & Assignment API Tester';

  // ─── Auth (matches backend user "kisol") ──────────────
  private defaultAuth = btoa('kisol:kisol123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'kisol' && storedHeader) {
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
  driverEndpoints = [
    { method: 'POST', path: '/drivers', desc: 'Register delivery driver' },
    { method: 'GET', path: '/drivers', desc: 'List all drivers' },
    { method: 'GET', path: '/drivers/{driverId}', desc: 'View driver profile' },
    { method: 'PUT', path: '/drivers/{driverId}', desc: 'Update driver information' },
    { method: 'DELETE', path: '/drivers/{driverId}', desc: 'Deactivate driver' },
  ];

  assignmentEndpoints = [
    { method: 'PUT', path: '/orders/{orderId}/assign-driver/{driverId}', desc: 'Assign driver to order' },
    { method: 'GET', path: '/drivers/{driverId}/orders', desc: 'List orders assigned to driver' },
    { method: 'PUT', path: '/orders/{orderId}/delivery-status', desc: 'Update delivery status' },
  ];

  // ─── State ────────────────────────────────────────────
  drivers: any[] = [];
  driverOrders: any[] = [];
  singleDriver: any = null;

  driverActionId = '';
  driverIdForOrders = '';
  orderIdForAssignment = '';
  driverIdForAssignment = '';
  orderIdForStatus = '';
  deliveryStatus = 'OUT_FOR_DELIVERY';

  newDriver = {
    driverName: '',
    driverPhone: '',
    driverVehicle: ''
  };

  // Popup flags
  showDriversListPopup = false;
  showDriverFormPopup = false;
  showDriverIdPopup = false;
  showDriverDetailPopup = false;

  showAssignDriverPopup = false;
  showDriverOrdersPopup = false;        // collect driver ID
  showDriverOrdersListPopup = false;    // show fetched orders
  showDeliveryStatusPopup = false;

  driverActionType = '';
  driverActionTitle = '';

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
  // All assignment endpoints contain "/orders" in the path
  if (ep.path.includes('/orders')) {
    this.handleAssignment(ep);
  } else {
    this.handleDriver(ep);
  }
}

  // ─── Driver handlers ──────────────────────────────────
  handleDriver(ep: any) {
    const url = `${this.baseUrl}/drivers`;

    if (ep.method === 'GET' && ep.path === '/drivers') {
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.drivers = this.extractData(res);
          this.showDriversListPopup = true;
          this.showToastMessage('Drivers loaded', 'success');
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (ep.method === 'POST') {
      this.resetDriverForm();
      this.driverActionType = 'POST';
      this.showDriverFormPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.driverActionType = 'PUT';
      this.driverActionTitle = 'Update Driver – Enter ID';
      this.driverActionId = '';
      this.showDriverIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.driverActionType = 'DELETE';
      this.driverActionTitle = 'Delete Driver – Enter ID';
      this.driverActionId = '';
      this.showDriverIdPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path === '/drivers/{driverId}') {
      this.driverActionType = 'GET_BY_ID';
      this.driverActionTitle = 'View Driver Profile – Enter ID';
      this.driverActionId = '';
      this.showDriverIdPopup = true;
      return;
    }
  }

  confirmDriverIdAction() {
    const id = this.safe(this.driverActionId);
    const url = `${this.baseUrl}/drivers`;

    if (!id) {
      this.showToastMessage('Driver ID is required', 'error');
      return;
    }

    if (this.driverActionType === 'GET_BY_ID') {
      this.http.get<any>(`${url}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleDriver = this.extractData(res)[0];
          this.showDriverIdPopup = false;
          this.showDriverDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.driverActionType === 'DELETE') {
      this.http.delete(`${url}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Driver Deleted ✅', 'success');
          this.showDriverIdPopup = false;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.driverActionType === 'PUT') {
      this.http.get<any>(`${url}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newDriver = {
            driverName: data.driverName,
            driverPhone: data.driverPhone,
            driverVehicle: data.driverVehicle
          };
          this.showDriverIdPopup = false;
          this.showDriverFormPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  submitDriverForm() {
    const id = this.safe(this.driverActionId);
    const url = `${this.baseUrl}/drivers`;

    const payload = { ...this.newDriver };

    if (this.driverActionType === 'POST') {
      this.http.post(url, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Driver Registered ✅', 'success');
          this.closeDriverPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.driverActionType === 'PUT') {
      this.http.put(`${url}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Driver Updated ✅', 'success');
          this.closeDriverPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  resetDriverForm() {
    this.newDriver = { driverName: '', driverPhone: '', driverVehicle: '' };
  }

  closeDriverPopups() {
    this.showDriverFormPopup = false;
    this.showDriverIdPopup = false;
    this.showDriverDetailPopup = false;
    this.driverActionId = '';
    this.resetDriverForm();
  }

  // ─── Assignment handlers ──────────────────────────────
  handleAssignment(ep: any) {
    if (ep.path === '/orders/{orderId}/assign-driver/{driverId}') {
      this.orderIdForAssignment = '';
      this.driverIdForAssignment = '';
      this.showAssignDriverPopup = true;
      return;
    }

    if (ep.path === '/drivers/{driverId}/orders') {
      this.driverIdForOrders = '';
      this.showDriverOrdersPopup = true;    // just the ID input
      return;
    }

    if (ep.path === '/orders/{orderId}/delivery-status') {
      this.orderIdForStatus = '';
      this.deliveryStatus = 'OUT_FOR_DELIVERY';
      this.showDeliveryStatusPopup = true;
      return;
    }
  }

  submitAssignDriver() {
    const orderId = this.safe(this.orderIdForAssignment);
    const driverId = this.safe(this.driverIdForAssignment);
    if (!orderId || !driverId) {
      this.showToastMessage('Both Order ID and Driver ID are required', 'error');
      return;
    }
    const url = `${this.baseUrl}/orders/${orderId}/assign-driver/${driverId}`;
    this.http.put(url, {}, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Driver assigned to order ✅', 'success');
        this.showAssignDriverPopup = false;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  submitGetDriverOrders() {
    const driverId = this.safe(this.driverIdForOrders);
    if (!driverId) {
      this.showToastMessage('Driver ID is required', 'error');
      return;
    }
    const url = `${this.baseUrl}/drivers/${driverId}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.driverOrders = this.extractData(res);
        this.showDriverOrdersPopup = false;
        this.showDriverOrdersListPopup = true;   // show the list
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  submitDeliveryStatus() {
    const orderId = this.safe(this.orderIdForStatus);
    if (!orderId) {
      this.showToastMessage('Order ID is required', 'error');
      return;
    }
    const url = `${this.baseUrl}/orders/${orderId}/delivery-status`;
    const payload = { status: this.deliveryStatus };
    this.http.put(url, payload, this.authHeader()).subscribe({
      next: () => {
        this.showToastMessage('Delivery status updated ✅', 'success');
        this.showDeliveryStatusPopup = false;
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }
}