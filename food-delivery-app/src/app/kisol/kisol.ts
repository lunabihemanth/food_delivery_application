import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-kisol',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './kisol.html',
  styleUrls: ['./kisol.css']
})
export class Kisol {

  constructor(private http: HttpClient) {}

  baseUrl = 'http://localhost:8081';
  name = 'Kisol';
  role = 'Driver & Assignment API Tester';

  // ================= ENDPOINTS DISPLAY =================
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

  // ================= STATE =================
  drivers: any[] = [];
  driverOrders: any[] = [];
  singleDriver: any = null;

  driverActionId = '';
  driverIdForOrders = '';
  orderIdForAssignment = '';
  driverIdForAssignment = '';
  orderIdForStatus = '';
  deliveryStatus = 'OUT_FOR_DELIVERY';

  // ✅ Changed vehicleNumber → driverVehicle
  newDriver = {
    driverName: '',
    driverPhone: '',
    driverVehicle: ''
  };

  // ================= POPUP FLAGS =================
  showDriversListPopup = false;
  showDriverFormPopup = false;
  showDriverIdPopup = false;
  showDriverDetailPopup = false;

  showAssignDriverPopup = false;
  showDriverOrdersPopup = false;
  showDeliveryStatusPopup = false;

  driverActionType = '';
  driverActionTitle = '';

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

  // ================= DRIVER HANDLERS =================
  handleDriver(ep: any) {
    const url = `${this.baseUrl}/drivers`;

    if (ep.method === 'GET' && ep.path === '/drivers') {
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.drivers = this.extractData(res);
          this.showDriversListPopup = true;
        },
        error: (err) => this.showError(err)
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
      alert('Driver ID is required');
      return;
    }

    if (this.driverActionType === 'GET_BY_ID') {
      this.http.get<any>(`${url}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleDriver = this.extractData(res)[0];
          this.showDriverIdPopup = false;
          this.showDriverDetailPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.driverActionType === 'DELETE') {
      this.http.delete(`${url}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert('Driver Deleted ✅');
          this.showDriverIdPopup = false;
        },
        error: (err) => this.showError(err)
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
            driverVehicle: data.driverVehicle    // ✅ Updated
          };
          this.showDriverIdPopup = false;
          this.showDriverFormPopup = true;
        },
        error: (err) => this.showError(err)
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
          alert('Driver Registered ✅');
          this.closeDriverPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.driverActionType === 'PUT') {
      this.http.put(`${url}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Driver Updated ✅');
          this.closeDriverPopups();
        },
        error: (err) => this.showError(err)
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

  // ================= ASSIGNMENT HANDLERS =================
  handleAssignment(ep: any) {
    if (ep.path === '/orders/{orderId}/assign-driver/{driverId}') {
      this.orderIdForAssignment = '';
      this.driverIdForAssignment = '';
      this.showAssignDriverPopup = true;
      return;
    }

    if (ep.path === '/drivers/{driverId}/orders') {
      this.driverIdForOrders = '';
      this.showDriverOrdersPopup = true;
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
      alert('Both Order ID and Driver ID are required');
      return;
    }
    const url = `${this.baseUrl}/orders/${orderId}/assign-driver/${driverId}`;
    this.http.put(url, {}, this.authHeader()).subscribe({
      next: () => {
        alert('Driver assigned to order ✅');
        this.showAssignDriverPopup = false;
      },
      error: (err) => this.showError(err)
    });
  }

  submitGetDriverOrders() {
    const driverId = this.safe(this.driverIdForOrders);
    if (!driverId) {
      alert('Driver ID is required');
      return;
    }
    const url = `${this.baseUrl}/drivers/${driverId}/orders`;
    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.driverOrders = this.extractData(res);
      },
      error: (err) => this.showError(err)
    });
  }

  submitDeliveryStatus() {
    const orderId = this.safe(this.orderIdForStatus);
    if (!orderId) {
      alert('Order ID is required');
      return;
    }
    const url = `${this.baseUrl}/orders/${orderId}/delivery-status`;
    const payload = { status: this.deliveryStatus };
    this.http.put(url, payload, this.authHeader()).subscribe({
      next: () => {
        alert('Delivery status updated ✅');
        this.showDeliveryStatusPopup = false;
      },
      error: (err) => this.showError(err)
    });
  }
}