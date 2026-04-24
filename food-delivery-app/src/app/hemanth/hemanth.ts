import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-hemanth',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './hemanth.html',
  styleUrls: ['./hemanth.css']
})
export class Hemanth {

  constructor(private http: HttpClient) {}

  baseUrl = 'http://localhost:8081';

  name = 'Hemanth';
  role = 'Restaurant & Menu API Tester';

  // ================= ENDPOINTS (display purposes) =================
  restaurantEndpoints = [
    { method: 'GET', path: '/restaurants', desc: 'List restaurants' },
    { method: 'POST', path: '/restaurants', desc: 'Add restaurant' },
    { method: 'PUT', path: '/restaurants/{id}', desc: 'Update restaurant' },
    { method: 'DELETE', path: '/restaurants/{id}', desc: 'Delete restaurant' },
  ];

  menuEndpoints = [
    { method: 'POST', path: '/menu-items', desc: 'Add menu item (restaurantId in body)' },
    { method: 'GET', path: '/menu-items/restaurants/{restaurantId}/menu-items', desc: 'View restaurant menu' },
    { method: 'GET', path: '/menu-items/{itemId}', desc: 'View menu item details' },
    { method: 'PUT', path: '/menu-items/{itemId}', desc: 'Update menu item' },
    { method: 'DELETE', path: '/menu-items/{itemId}', desc: 'Remove menu item' },
  ];

  // ================= STATE =================
  restaurants: any[] = [];
  menuItems: any[] = [];
  singleMenuItem: any = null;

  restaurantActionId = '';
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

  // ================= POPUP FLAGS =================
  showRestaurantsPopup = false;
  showRestaurantFormPopup = false;
  showRestaurantIdPopup = false;

  showMenuGetPopup = false;
  showMenuFormPopup = false;
  showMenuIdPopup = false;
  showMenuItemDetailPopup = false;

  restaurantActionType = '';
  menuActionType = '';
  menuActionTitle = '';

  // ================= AUTH =================
  authHeader() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Basic ' + btoa('hemanth:hemanth123'),
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

  // ================= RESTAURANTS (unchanged) =================
  handleRestaurant(ep: any) {
    const url = `${this.baseUrl}/restaurants`;

    if (ep.method === 'GET') {
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.restaurants = this.extractData(res);
          this.showRestaurantsPopup = true;
        },
        error: (err) => this.showError(err)
      });
    }

    if (ep.method === 'POST') {
      this.resetRestaurantForm();
      this.restaurantActionType = 'POST';
      this.showRestaurantFormPopup = true;
    }

    if (ep.method === 'PUT') {
      this.restaurantActionType = 'PUT';
      this.restaurantActionId = '';
      this.showRestaurantIdPopup = true;
    }

    if (ep.method === 'DELETE') {
      this.restaurantActionType = 'DELETE';
      this.restaurantActionId = '';
      this.showRestaurantIdPopup = true;
    }
  }

  submitRestaurant() {
    const id = this.safe(this.restaurantActionId);
    const url = `${this.baseUrl}/restaurants`;

    if (this.restaurantActionType === 'POST') {
      this.http.post(url, this.newRestaurant, this.authHeader()).subscribe({
        next: () => {
          alert("Restaurant Added ✅");
          this.closeAllRestaurantPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.restaurantActionType === 'PUT' && this.showRestaurantIdPopup) {
      if (!id) {
        alert("Restaurant ID required");
        return;
      }
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
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.restaurantActionType === 'PUT' && this.showRestaurantFormPopup) {
      this.http.put(`${url}/${id}`, this.newRestaurant, this.authHeader()).subscribe({
        next: () => {
          alert("Restaurant Updated ✅");
          this.closeAllRestaurantPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    if (this.restaurantActionType === 'DELETE') {
      this.http.delete(`${url}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert("Restaurant Deleted ✅");
          this.showRestaurantIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
    }
  }

  resetRestaurantForm() {
    this.newRestaurant = { restaurantName: '', restaurantAddress: '', restaurantPhone: '' };
  }

  closeAllRestaurantPopups() {
    this.showRestaurantFormPopup = false;
    this.showRestaurantIdPopup = false;
    this.restaurantActionId = '';
    this.resetRestaurantForm();
  }

  // ================= MENU (updated URLs to match backend) =================
  handleMenu(ep: any) {
    const rid = this.safe(this.menuRestaurantId);
    if (!rid) {
      alert('Please enter a Restaurant ID first');
      return;
    }

    // POST /menu-items (restaurantId in body)
    if (ep.method === 'POST' && ep.path === '/menu-items') {
      this.resetMenuForm();
      this.menuActionType = 'POST';
      this.showMenuFormPopup = true;
      return;
    }

    // GET /menu-items/restaurants/{restaurantId}/menu-items
    if (ep.method === 'GET' && ep.path.includes('/restaurants/')) {
      const url = `${this.baseUrl}/menu-items/restaurants/${rid}/menu-items`;
      this.http.get<any>(url, this.authHeader()).subscribe({
        next: (res) => {
          this.menuItems = this.extractData(res);
          this.showMenuGetPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // GET /menu-items/{itemId}
    if (ep.method === 'GET' && ep.path === '/menu-items/{itemId}') {
      this.menuActionType = 'GET_BY_ID';
      this.menuActionTitle = 'View Menu Item Details – Enter ID';
      this.menuItemId = '';
      this.showMenuIdPopup = true;
      return;
    }

    // PUT /menu-items/{itemId}
    if (ep.method === 'PUT') {
      this.menuActionType = 'PUT';
      this.menuActionTitle = 'Update Menu Item – Enter ID';
      this.menuItemId = '';
      this.showMenuIdPopup = true;
      return;
    }

    // DELETE /menu-items/{itemId}
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
    const rid = this.safe(this.menuRestaurantId);
    const base = `${this.baseUrl}/menu-items`;

    if (!id) {
      alert('Menu Item ID is required');
      return;
    }

    // GET by ID
    if (this.menuActionType === 'GET_BY_ID') {
      this.http.get<any>(`${base}/${id}`, this.authHeader()).subscribe({
        next: (res) => {
          this.singleMenuItem = this.extractData(res)[0];
          this.showMenuIdPopup = false;
          this.showMenuItemDetailPopup = true;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // DELETE
    if (this.menuActionType === 'DELETE') {
      this.http.delete(`${base}/${id}`, this.authHeader()).subscribe({
        next: () => {
          alert("Menu Item Deleted ✅");
          this.showMenuIdPopup = false;
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // PUT Step 1: fetch existing item
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
        error: (err) => this.showError(err)
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

    // POST /menu-items
    if (this.menuActionType === 'POST') {
      this.http.post(base, payload, this.authHeader()).subscribe({
        next: () => {
          alert("Menu Item Added ✅");
          this.closeMenuPopups();
        },
        error: (err) => this.showError(err)
      });
      return;
    }

    // PUT /menu-items/{itemId}
    if (this.menuActionType === 'PUT') {
      this.http.put(`${base}/${id}`, payload, this.authHeader()).subscribe({
        next: () => {
          alert("Menu Item Updated ✅");
          this.closeMenuPopups();
        },
        error: (err) => this.showError(err)
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