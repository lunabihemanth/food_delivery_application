import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-annie',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './annie.html',
  styleUrls: ['./annie.css']
})
export class Annie {

  constructor(private http: HttpClient) {}

  baseUrl = 'http://localhost:8081';
  name = 'Annie';
  role = 'Customer & Address API Tester';

  // ================= ENDPOINTS DISPLAY =================
  customerEndpoints = [
    { method: 'POST', path: '/customers/add', desc: 'Create a new customer' },
    { method: 'GET', path: '/customers/getall', desc: 'Fetch all customers (paging & filtering ready)' },
    { method: 'GET', path: '/customers/{customerId}', desc: 'Fetch customer details by ID' },
    { method: 'PUT', path: '/customers/{customerId}', desc: 'Update customer profile' },
    { method: 'DELETE', path: '/customers/{customerId}', desc: 'Deactivate/delete customer' },
  ];

  addressEndpoints = [
    { method: 'POST', path: '/customers/{customerId}/addresses', desc: 'Add a delivery address for customer' },
    { method: 'GET', path: '/customers/{customerId}/addresses', desc: 'List all delivery addresses for a customer' },
    { method: 'GET', path: '/addresses/{addressId}', desc: 'Fetch a specific address' },
    { method: 'PUT', path: '/addresses/{addressId}', desc: 'Update address' },
    { method: 'DELETE', path: '/addresses/{addressId}', desc: 'Remove address' },
  ];

  // ================= STATE =================
  customers: any[] = [];
  addresses: any[] = [];
  singleCustomer: any = null;
  singleAddress: any = null;

  // For customer operations
  customerActionId = '';
  customerActionType = '';
  customerActionTitle = '';

  // For address operations (context customer ID)
  contextCustomerId = '';
  addressActionId = '';
  addressActionType = '';
  addressActionTitle = '';

  newCustomer = {
    customerName: '',
    customerEmail: '',
    customerPhone: ''
  };

  newAddress = {
    addressLine1: '',
    addressLine2: '',
    city: '',
    state: '',
    postalCode: '',
    country: ''
  };

  // ================= POPUP FLAGS =================
  showCustomersListPopup = false;
  showCustomerFormPopup = false;
  showCustomerIdPopup = false;
  showCustomerDetailPopup = false;

  showAddressesListPopup = false;
  showAddressFormPopup = false;
  showAddressIdPopup = false;
  showAddressDetailPopup = false;

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

  // ================= CUSTOMER HANDLERS =================
  handleCustomer(ep: any) {
    const base = `${this.baseUrl}/customers`;

    // GET ALL → /customers/getall
    if (ep.method === 'GET' && ep.path === '/customers/getall') {
      this.http.get<any>(`${base}/getall`, this.authHeader()).subscribe({
        next: (res) => {
          this.customers = this.extractData(res);
          this.showCustomersListPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // POST → /customers/add
    if (ep.method === 'POST') {
      this.resetCustomerForm();
      this.customerActionType = 'POST';
      this.showCustomerFormPopup = true;
      return;
    }

    // PUT → /customers/{customerId}
    if (ep.method === 'PUT') {
      this.customerActionType = 'PUT';
      this.customerActionTitle = 'Update Customer – Enter ID';
      this.customerActionId = '';
      this.showCustomerIdPopup = true;
      return;
    }

    // DELETE → /customers/{customerId}
    if (ep.method === 'DELETE') {
      this.customerActionType = 'DELETE';
      this.customerActionTitle = 'Delete Customer – Enter ID';
      this.customerActionId = '';
      this.showCustomerIdPopup = true;
      return;
    }

    // GET BY ID → /customers/{customerId}
    if (ep.method === 'GET' && ep.path === '/customers/{customerId}') {
      this.customerActionType = 'GET_BY_ID';
      this.customerActionTitle = 'View Customer Details – Enter ID';
      this.customerActionId = '';
      this.showCustomerIdPopup = true;
      return;
    }
  }

  confirmCustomerIdAction() {
    const id = this.safe(this.customerActionId);
    const base = `${this.baseUrl}/customers`;

    if (!id) {
      alert('Customer ID is required');
      return;
    }

    // GET BY ID
    if (this.customerActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleCustomer = this.extractData(res)[0];
          this.showCustomerIdPopup = false;
          this.showCustomerDetailPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // DELETE
    if (this.customerActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert('Customer Deleted ✅');
          this.showCustomerIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // PUT Step 1: fetch existing customer
    if (this.customerActionType === 'PUT') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newCustomer = {
            customerName: data.customerName,
            customerEmail: data.customerEmail,
            customerPhone: data.customerPhone
          };
          this.showCustomerIdPopup = false;
          this.showCustomerFormPopup = true;
        },
        error: (err) => this.showError(err)
      });
    }
  }

  submitCustomerForm() {
    const id = this.safe(this.customerActionId);
    const base = `${this.baseUrl}/customers`;

    const payload = { ...this.newCustomer };

    // POST → /customers/add
    if (this.customerActionType === 'POST') {
      this.http.post(`${base}/add`, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Customer Created ✅');
          this.closeCustomerPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // PUT → /customers/{id}
    if (this.customerActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Customer Updated ✅');
          this.closeCustomerPopups();
        },
        error: (err) => this.showError(err)
      });
    }
  }

  resetCustomerForm() {
    this.newCustomer = { customerName: '', customerEmail: '', customerPhone: '' };
  }

  closeCustomerPopups() {
    this.showCustomerFormPopup = false;
    this.showCustomerIdPopup = false;
    this.showCustomerDetailPopup = false;
    this.customerActionId = '';
    this.resetCustomerForm();
  }

  // ================= ADDRESS HANDLERS (unchanged) =================
  handleAddress(ep: any) {
    const cid = this.safe(this.contextCustomerId);
    if (!cid) {
      alert('Please enter a Customer ID (context) first');
      return;
    }

    // POST /customers/{customerId}/addresses
    if (ep.method === 'POST' && ep.path.includes('/customers/')) {
      this.resetAddressForm();
      this.addressActionType = 'POST';
      this.showAddressFormPopup = true;
      return;
    }

    // GET /customers/{customerId}/addresses
    if (ep.method === 'GET' && ep.path.includes('/customers/')) {
      const url = `${this.baseUrl}/customers/${cid}/addresses`;
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.addresses = this.extractData(res);
          this.showAddressesListPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // GET /addresses/{addressId}
    if (ep.method === 'GET' && ep.path === '/addresses/{addressId}') {
      this.addressActionType = 'GET_BY_ID';
      this.addressActionTitle = 'View Address Details – Enter Address ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }

    // PUT /addresses/{addressId}
    if (ep.method === 'PUT') {
      this.addressActionType = 'PUT';
      this.addressActionTitle = 'Update Address – Enter Address ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }

    // DELETE /addresses/{addressId}
    if (ep.method === 'DELETE') {
      this.addressActionType = 'DELETE';
      this.addressActionTitle = 'Delete Address – Enter Address ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }
  }

  confirmAddressIdAction() {
    const id = this.safe(this.addressActionId);
    const base = `${this.baseUrl}/addresses`;

    if (!id) {
      alert('Address ID is required');
      return;
    }

    if (this.addressActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleAddress = this.extractData(res)[0];
          this.showAddressIdPopup = false;
          this.showAddressDetailPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.addressActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert('Address Deleted ✅');
          this.showAddressIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.addressActionType === 'PUT') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newAddress = {
            addressLine1: data.addressLine1,
            addressLine2: data.addressLine2 || '',
            city: data.city,
            state: data.state,
            postalCode: data.postalCode,
            country: data.country
          };
          this.showAddressIdPopup = false;
          this.showAddressFormPopup = true;
        },
        error: (err) => this.showError(err)
      });
    }
  }

  submitAddressForm() {
    const cid = this.safe(this.contextCustomerId);
    const id = this.safe(this.addressActionId);
    const base = `${this.baseUrl}/addresses`;

    const payload = { ...this.newAddress, customerId: cid };

    if (this.addressActionType === 'POST') {
      const url = `${this.baseUrl}/customers/${cid}/addresses`;
      this.http.post(url, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Address Added ✅');
          this.closeAddressPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.addressActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          alert('Address Updated ✅');
          this.closeAddressPopups();
        },
        error: (err) => this.showError(err)
      });
    }
  }

  resetAddressForm() {
    this.newAddress = {
      addressLine1: '',
      addressLine2: '',
      city: '',
      state: '',
      postalCode: '',
      country: ''
    };
  }

  closeAddressPopups() {
    this.showAddressFormPopup = false;
    this.showAddressIdPopup = false;
    this.showAddressDetailPopup = false;
    this.addressActionId = '';
    this.resetAddressForm();
  }
}