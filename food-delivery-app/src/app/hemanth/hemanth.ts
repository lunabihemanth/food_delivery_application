import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-hemanth',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './hemanth.html',
  styleUrls: ['./hemanth.css']
})
export class Hemanth {
  constructor(private http: HttpClient, private location: Location) {}

  baseUrl = 'http://localhost:8081';
  name = 'Hemanth Karthik M';
  role = 'Restaurant & Menu API Tester';

  // ─── Auth (matches backend user "hemanth") ──────────────
  private defaultAuth = btoa('hemanth:hemanth123');

  authHeader() {
    const storedUser = localStorage.getItem('username');
    const storedHeader = localStorage.getItem('authHeader');
    if (storedUser === 'hemanth' && storedHeader) {
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
  restaurantEndpoints = [
    { method: 'GET', path: '/restaurants', desc: 'List restaurants' },
    { method: 'POST', path: '/restaurants', desc: 'Add restaurant' },
    { method: 'GET', path: '/restaurants/{restaurantId}', desc: 'View restaurant details' },
    { method: 'PUT', path: '/restaurants/{restaurantId}', desc: 'Update restaurant' },
    { method: 'DELETE', path: '/restaurants/{restaurantId}', desc: 'Delete restaurant' },
  ];

  menuEndpoints = [
    { method: 'POST', path: '/menu-items', desc: 'Add menu item (restaurantId in body)' },
    { method: 'GET', path: '/menu-items/restaurants/{restaurantId}/menu-items', desc: 'View restaurant menu' },
    { method: 'GET', path: '/menu-items/{itemId}', desc: 'View menu item details' },
    { method: 'PUT', path: '/menu-items/{itemId}', desc: 'Update menu item' },
    { method: 'DELETE', path: '/menu-items/{itemId}', desc: 'Remove menu item' },
  ];

  // ─── State ────────────────────────────────────────────
  restaurants: any[] = [];
  singleRestaurant: any = null;
  menuItems: any[] = [];
  singleMenuItem: any = null;

  restaurantActionId = '';
  restaurantActionTitle = '';
  menuRestaurantId = '';
  menuItemId = '';

  newRestaurant = {
    restaurantName: '',
    restaurantAddress: '',
    restaurantPhone: ''
  };

  newMenu = {
    itemName: '',
    itemDescription: '',
    itemPrice: ''
  };

  showRestaurantsPopup = false;
  showRestaurantDetailPopup = false;
  showRestaurantFormPopup = false;
  showRestaurantIdPopup = false;
  showMenuGetPopup = false;
  showMenuFormPopup = false;
  showMenuIdPopup = false;
  showMenuItemDetailPopup = false;
  showMenuRestaurantIdPopup = false;

  restaurantActionType = '';
  menuActionType = '';
  menuActionTitle = '';
  pendingMenuEndpoint: any = null;

  // ─── Search state ─────────────────────────────────────
  restaurantSearchTerm = '';

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

  handleEndpoint(ep: any) {
    if (ep.path.startsWith('/restaurants')) {
      this.handleRestaurant(ep);
    } else if (ep.path === '/menu-items') {
      this.pendingMenuEndpoint = ep;
      this.menuRestaurantId = '';
      this.showMenuRestaurantIdPopup = true;
    } else if (ep.path.includes('/restaurants/')) {
      this.pendingMenuEndpoint = ep;
      this.menuRestaurantId = '';
      this.showMenuRestaurantIdPopup = true;
    } else {
      this.handleMenu(ep);
    }
  }

  confirmMenuRestaurantId() {
    const rid = this.safe(this.menuRestaurantId);
    if (!rid) {
      this.showToastMessage('Restaurant ID is required', 'error');
      return;
    }
    this.showMenuRestaurantIdPopup = false;
    if (this.pendingMenuEndpoint) {
      if (this.pendingMenuEndpoint.path === '/menu-items') {
        this.resetMenuForm();
        this.menuActionType = 'POST';
        this.showMenuFormPopup = true;
      } else if (this.pendingMenuEndpoint.path.includes('/restaurants/')) {
        const url = `${this.baseUrl}/menu-items/restaurants/${rid}/menu-items`;
        this.http.get<any>(url, this.authHeader()).subscribe({
          next: (res) => {
            this.menuItems = this.extractData(res);
            this.showMenuGetPopup = true;
            this.showToastMessage('Menu items loaded', 'success');
          },
          error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
        });
      }
      this.pendingMenuEndpoint = null;
    }
  }

  // ─── Fetch restaurants (with optional search) ─────────
  fetchRestaurants() {
    const base = `${this.baseUrl}/restaurants`;
    let url = base;
    if (this.restaurantSearchTerm.trim()) {
      url += '?restaurantName=' + encodeURIComponent(this.restaurantSearchTerm.trim());
    }

    this.http.get<any>(url, this.authHeader()).subscribe({
      next: (res) => {
        this.restaurants = this.extractData(res);
        // The popup flag is already true when this method is called
        this.showToastMessage('Restaurants loaded', 'success');
      },
      error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
    });
  }

  // ─── Restaurant handlers ──────────────────────────────
  handleRestaurant(ep: any) {
    const url = `${this.baseUrl}/restaurants`;

    if (ep.method === 'GET' && ep.path === '/restaurants') {
      this.restaurantSearchTerm = '';
      this.restaurants = [];
      this.showRestaurantsPopup = true;      // opens the popup immediately
      this.fetchRestaurants();               // loads all restaurants (no filter)
      return;
    }

    if (ep.method === 'POST') {
      this.resetRestaurantForm();
      this.restaurantActionType = 'POST';
      this.showRestaurantFormPopup = true;
      return;
    }

    if (ep.method === 'GET' && ep.path === '/restaurants/{restaurantId}') {
      this.restaurantActionType = 'GET_BY_ID';
      this.restaurantActionTitle = 'View Restaurant Details – Enter ID';
      this.restaurantActionId = '';
      this.showRestaurantIdPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.restaurantActionType = 'PUT';
      this.restaurantActionTitle = 'Update Restaurant – Enter ID';
      this.restaurantActionId = '';
      this.showRestaurantIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.restaurantActionType = 'DELETE';
      this.restaurantActionTitle = 'Delete Restaurant – Enter ID';
      this.restaurantActionId = '';
      this.showRestaurantIdPopup = true;
      return;
    }
  }

  submitRestaurant() {
    const id = this.safe(this.restaurantActionId);
    const url = `${this.baseUrl}/restaurants`;

    if (this.restaurantActionType === 'POST') {
  // Quick front-end check to avoid backend validation errors
  const name = this.newRestaurant.restaurantName.trim();
  const addr = this.newRestaurant.restaurantAddress.trim();
  const phone = this.newRestaurant.restaurantPhone.trim();
  if (!name || name.length < 2 || !addr || !phone || !/^\d{10}$/.test(phone)) {
    this.showToastMessage(
      'Please fill all required fields correctly:\n• Name (2‑100 characters)\n• Address\n• Phone (10 digits)',
      'error'
    );
    return;
  }

  this.http.post(url, this.newRestaurant, this.authHeader()).subscribe({
    next: () => {
      this.showToastMessage('Restaurant Added ✅', 'success');
      this.closeAllRestaurantPopups();
    },
    error: (err) => {
      // If the backend still returns a 400, show the same simple message
      if (err.status === 400) {
        this.showToastMessage(
          'Please fill all required fields correctly.\nName (2‑100 chars), Address, Phone (10 digits).',
          'error'
        );
      } else {
        this.showToastMessage(err?.error?.message ?? err?.message, 'error');
      }
    }
  });
  return;
}

    if (this.restaurantActionType === 'GET_BY_ID') {
      if (!id) return this.showToastMessage('Restaurant ID required', 'error');
      this.http.get<any>(`${url}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleRestaurant = this.extractData(res)[0];
          this.showRestaurantIdPopup = false;
          this.showRestaurantDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.restaurantActionType === 'PUT' && this.showRestaurantIdPopup) {
      if (!id) return this.showToastMessage('Restaurant ID required', 'error');
      this.http.get<any>(`${url}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newRestaurant = {
            restaurantName: data.restaurantName,
            restaurantAddress: data.restaurantAddress,
            restaurantPhone: data.restaurantPhone
          };
          this.showRestaurantIdPopup = false;
          this.showRestaurantFormPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.restaurantActionType === 'PUT' && this.showRestaurantFormPopup) {
      this.http.put(`${url}/${id}`, this.newRestaurant, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Restaurant Updated ✅', 'success');
          this.closeAllRestaurantPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.restaurantActionType === 'DELETE') {
      this.http.delete(`${url}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Restaurant Deleted ✅', 'success');
          this.showRestaurantIdPopup = false;
        },
        error: (err) => this.showToastMessage("Restaurant ID is missing or invalid", 'error')
      });
    }
  }

  resetRestaurantForm() {
    this.newRestaurant = { restaurantName: '', restaurantAddress: '', restaurantPhone: '' };
  }

  closeAllRestaurantPopups() {
    this.showRestaurantFormPopup = false;
    this.showRestaurantIdPopup = false;
    this.showRestaurantDetailPopup = false;
    this.restaurantActionId = '';
    this.resetRestaurantForm();
  }

  // ─── Menu handlers (unchanged) ────────────────────────
  handleMenu(ep: any) {
    if (ep.method === 'GET' && ep.path === '/menu-items/{itemId}') {
      this.menuActionType = 'GET_BY_ID';
      this.menuActionTitle = 'View Menu Item Details – Enter ID';
      this.menuItemId = '';
      this.showMenuIdPopup = true;
      return;
    }

    if (ep.method === 'PUT') {
      this.menuActionType = 'PUT';
      this.menuActionTitle = 'Update Menu Item – Enter ID';
      this.menuItemId = '';
      this.showMenuIdPopup = true;
      return;
    }

    if (ep.method === 'DELETE') {
      this.menuActionType = 'DELETE';
      this.menuActionTitle = 'Delete Menu Item – Enter ID';
      this.menuItemId = '';
      this.showMenuIdPopup = true;
      return;
    }
  }

  confirmMenuAction() {
    const id = this.safe(this.menuItemId);
    const base = `${this.baseUrl}/menu-items`;

    if (!id) {
      this.showToastMessage('Menu Item ID is required', 'error');
      return;
    }

    if (this.menuActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleMenuItem = this.extractData(res)[0];
          this.showMenuIdPopup = false;
          this.showMenuItemDetailPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.menuActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Menu Item Deleted ✅', 'success');
          this.showMenuIdPopup = false;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.menuActionType === 'PUT') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          const data = this.extractData(res)[0];
          this.newMenu = {
            itemName: data.itemName,
            itemDescription: data.itemDescription,
            itemPrice: data.itemPrice
          };
          this.showMenuIdPopup = false;
          this.showMenuFormPopup = true;
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  submitMenuForm() {
    const rid = this.safe(this.menuRestaurantId);
    const id = this.safe(this.menuItemId);
    const base = `${this.baseUrl}/menu-items`;

    const payload = {
      ...this.newMenu,
      itemPrice: parseFloat(this.newMenu.itemPrice),
      restaurantId: rid
    };

    if (this.menuActionType === 'POST') {
      this.http.post(base, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Menu Item Added ✅', 'success');
          this.closeMenuPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
      return;
    }

    if (this.menuActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          this.showToastMessage('Menu Item Updated ✅', 'success');
          this.closeMenuPopups();
        },
        error: (err) => this.showToastMessage(err?.error?.message ?? err?.message, 'error')
      });
    }
  }

  resetMenuForm() {
    this.newMenu = { itemName: '', itemDescription: '', itemPrice: '' };
  }

  closeMenuPopups() {
    this.showMenuFormPopup = false;
    this.showMenuIdPopup = false;
    this.showMenuItemDetailPopup = false;
    this.menuItemId = '';
    this.resetMenuForm();
  }
}