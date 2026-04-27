import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-annie',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './annie.html',
  styleUrls: ['./annie.css']
})
export class Annie {

  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Annie Rufina C';
  role = 'Customer & Address API Tester';

  // ─── Auth (matches backend user "annie") ──────────────
  private defaultAuth = btoa('annie:annie123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'annie' && storedHeader) {
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
  customerEndpoints = [
    { method: 'POST', path: '/customers/add', desc: 'Create a new customer' },
    { method: 'GET', path: '/customers/getall', desc: 'Fetch all customers' },
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

  // ─── State ────────────────────────────────────────────
  customers: any[] = [];
  addresses: any[] = [];
  singleCustomer: any = null;
  singleAddress: any = null;

  customerActionId = '';
  customerActionType = '';
  customerActionTitle = '';

  // For address context
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

  // Popup flags
  showCustomersListPopup = false;
  showCustomerFormPopup = false;
  showCustomerIdPopup = false;
  showCustomerDetailPopup = false;

  showAddressesListPopup = false;
  showAddressFormPopup = false;
  showAddressIdPopup = false;
  showAddressDetailPopup = false;
  showAddressContextPopup = false;   // for collecting customer ID for address ops

  pendingEndpoint: any = null;      // store the endpoint that triggered the address context popup

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

  // ─── Fixed endpoint router ────────────────────────────
  handleEndpoint(ep: any) {
    // Address endpoints contain "/addresses" in their path
    if (ep.path.includes('/addresses') || ep.path.startsWith('/addresses/')) {
      // Those that need a customer ID first (POST and GET for a customer)
      if (ep.path.includes('/customers/')) {
        this.pendingEndpoint = ep;
        this.contextCustomerId = '';           // reset
        this.showAddressContextPopup = true;   // ask for customer ID first
      } else {
        // Direct address endpoints (/addresses/{addressId})
        this.pendingEndpoint = null;
        this.handleAddress(ep);
      }
    } else if (ep.path.startsWith('/customers')) {
      // All customer endpoints go here
      this.handleCustomer(ep);
    }
  }

  // Customer ID collection for address operations
  confirmAddressContext() {
    const cid = this.safe(this.contextCustomerId);
    if (!cid) {
      this.showToastMessage('Customer ID is required', 'error');
      return;
    }
    this.showAddressContextPopup = false;
    if (this.pendingEndpoint) {
      this.handleAddress(this.pendingEndpoint);  // continue with the collected ID
    }
  }

  // ─── Customer handlers (unchanged) ────────────────────
  handleCustomer(ep: any) {
    const base = `${this.baseUrl}/customers`;

    if (ep.method === 'GET' && ep.path === '/customers/getall') {
      this.http.get<any>(`${base}/getall`, this.authHeader()).subscribe({
        next: (res) => {
          this.customers = this.extractData(res);
          this.showCustomersListPopup = true;
          this.showToastMessage('Customers loaded', 'success');
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (ep.method === 'POST') {
      this.resetCustomerForm();
      this.customerActionType = 'POST';
      this.showCustomerFormPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.customerActionType = 'PUT';
      this.customerActionTitle = 'Update Customer – Enter ID';
      this.customerActionId = '';
      this.showCustomerIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.customerActionType = 'DELETE';
      this.customerActionTitle = 'Delete Customer – Enter ID';
      this.customerActionId = '';
      this.showCustomerIdPopup = true;
      return;
    }

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
      this.showToastMessage('Customer ID is required', 'error');
      return;
    }

    if (this.customerActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleCustomer = this.extractData(res)[0];
          this.showCustomerIdPopup = false;
          this.showCustomerDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.customerActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Customer Deleted ✅', 'success');
          this.showCustomerIdPopup = false;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

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
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  submitCustomerForm() {
    const id = this.safe(this.customerActionId);
    const base = `${this.baseUrl}/customers`;

    const payload = { ...this.newCustomer };

    if (this.customerActionType === 'POST') {
      this.http.post(`${base}/add`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Customer Created ✅', 'success');
          this.closeCustomerPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.customerActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Customer Updated ✅', 'success');
          this.closeCustomerPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
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

  // ─── Address handlers ─────────────────────────────────
  handleAddress(ep: any) {
    const cid = this.safe(this.contextCustomerId);

    // Endpoints that include /customers/ need a customer ID (we should already have it)
    if (ep.path.includes('/customers/')) {
      if (!cid) {
        this.showToastMessage('Customer ID is required', 'error');
        return;
      }

      if (ep.method === 'POST') {
        this.resetAddressForm();
        this.addressActionType = 'POST';
        this.showAddressFormPopup = true;
        return;
      }

      if (ep.method === 'GET') {
        const url = `${this.baseUrl}/customers/${cid}/addresses`;
        this.http.get<any>(url, this.authHeader()).subscribe({
          next: (res) => {
            this.addresses = this.extractData(res);
            this.showAddressesListPopup = true;
            this.showToastMessage('Addresses loaded', 'success');
          },
          error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
        });
        return;
      }
    }

    // Direct address ID endpoints (/addresses/{addressId})
    if (ep.method === 'GET' && ep.path === '/addresses/{addressId}') {
      this.addressActionType = 'GET_BY_ID';
      this.addressActionTitle = 'View Address Details – Enter ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.addressActionType = 'PUT';
      this.addressActionTitle = 'Update Address – Enter ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.addressActionType = 'DELETE';
      this.addressActionTitle = 'Delete Address – Enter ID';
      this.addressActionId = '';
      this.showAddressIdPopup = true;
      return;
    }
  }

  confirmAddressIdAction() {
    const id = this.safe(this.addressActionId);
    const base = `${this.baseUrl}/addresses`;

    if (!id) {
      this.showToastMessage('Address ID is required', 'error');
      return;
    }

    if (this.addressActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleAddress = this.extractData(res)[0];
          this.showAddressIdPopup = false;
          this.showAddressDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.addressActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Address Deleted ✅', 'success');
          this.showAddressIdPopup = false;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.addressActionType === 'PUT') {
      // Fetch existing address to pre-fill the form and to get its customerId
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
          // Store the customerId from the fetched address for later use
          this.contextCustomerId = data.customerId;
          this.showAddressIdPopup = false;
          this.showAddressFormPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  submitAddressForm() {
    const cid = this.safe(this.contextCustomerId);
    const id = this.safe(this.addressActionId);
    const base = `${this.baseUrl}/addresses`;

    if (!cid) {
      this.showToastMessage('Customer ID is required. Please re-open the endpoint.', 'error');
      return;
    }

    const payload = { ...this.newAddress, customerId: cid };

    if (this.addressActionType === 'POST') {
      const url = `${this.baseUrl}/customers/${cid}/addresses`;
      this.http.post(url, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Address Added ✅', 'success');
          this.closeAddressPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.addressActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Address Updated ✅', 'success');
          this.closeAddressPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
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