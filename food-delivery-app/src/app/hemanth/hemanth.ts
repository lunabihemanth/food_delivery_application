import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-hemanth',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './hemanth.html',
  styleUrl: './hemanth.css',
})
export class Hemanth {

  constructor(private http: HttpClient) {}

  baseUrl = 'http://localhost:8081';

  name = 'Hemanth';
  role = 'Restaurant & Menu API Tester';

  // ================= ENDPOINTS =================
  restaurantEndpoints = [
    { method: 'GET', path: '/restaurants', desc: 'List restaurants' },
    { method: 'POST', path: '/restaurants', desc: 'Add restaurant' },
    { method: 'PUT', path: '/restaurants/{id}', desc: 'Update restaurant' },
    { method: 'DELETE', path: '/restaurants/{id}', desc: 'Delete restaurant' },
  ];

  menuEndpoints = [
    { method: 'POST', path: '/menu-items', desc: 'Add menu item' },
    { method: 'GET', path: '/menu-items/restaurant/{restaurantId}', desc: 'Get menu items' },
    { method: 'GET', path: '/menu-items/{itemId}', desc: 'Get item by id' },
    { method: 'PUT', path: '/menu-items/{itemId}', desc: 'Update item' },
    { method: 'DELETE', path: '/menu-items/{itemId}', desc: 'Delete item' },
  ];

  // ================= STATE =================
  restaurants: any[] = [];
  menuItems: any[] = [];

  restaurantId = '';
  menuRestaurantId = '';
  menuItemId = '';
  restaurantActionId = '';

  newRestaurant = { restaurantName: '', restaurantAddress: '', restaurantPhone: '' };
  newMenu = { itemName: '', itemDescription: '', itemPrice: '' };

  // ================= POPUPS =================
  showRestaurantsPopup = false;
  showRestaurantFormPopup = false;
  showRestaurantIdPopup = false;

  showMenuGetPopup = false;
  showMenuFormPopup = false;
  showMenuIdPopup = false;

  restaurantActionType = '';
  menuActionType = '';
  menuActionTitle = '';
  restaurantActionTitle = '';

  // ================= AUTH =================
  authHeader() {
    return {
      headers: new HttpHeaders({
        Authorization: 'Basic ' + btoa('hemanth:hemanth123'),
        'Content-Type': 'application/json'
      })
    };
  }

  // =========================================================
  // RESTAURANTS
  // =========================================================
  handleRestaurant(ep: any) {

    const url = `${this.baseUrl}/restaurants`;

    if (ep.method === 'GET') {
      this.http.get<any>(url, this.authHeader())
        .subscribe(res => {
          this.restaurants = res.data || res;
          this.showRestaurantsPopup = true;
        });
    }

    if (ep.method === 'POST') {
      this.newRestaurant = { restaurantName: '', restaurantAddress: '', restaurantPhone: '' };
      this.restaurantActionType = 'POST';
      this.showRestaurantFormPopup = true;
    }

    if (ep.method === 'PUT') {
      this.restaurantActionType = 'PUT';
      this.restaurantActionTitle = "Enter Restaurant ID";
      this.showRestaurantIdPopup = true;
    }

    if (ep.method === 'DELETE') {
      this.restaurantActionType = 'DELETE';
      this.restaurantActionTitle = "Enter Restaurant ID";
      this.showRestaurantIdPopup = true;
    }
  }

  submitRestaurant() {

    const url = `${this.baseUrl}/restaurants`;

    if (this.restaurantActionType === 'POST') {
      this.http.post(url, this.newRestaurant, this.authHeader())
        .subscribe(() => {
          alert("Restaurant Added ✅");
          this.showRestaurantFormPopup = false;
        });
    }

    if (this.restaurantActionType === 'PUT') {
      this.http.put(
        `${url}/${this.restaurantActionId}`,
        this.newRestaurant,
        this.authHeader()
      ).subscribe(() => {
        alert("Restaurant Updated ✅");
        this.showRestaurantIdPopup = false;
      });
    }

    if (this.restaurantActionType === 'DELETE') {
      this.http.delete(
        `${url}/${this.restaurantActionId}`,
        this.authHeader()
      ).subscribe(() => {
        alert("Restaurant Deleted ✅");
        this.showRestaurantIdPopup = false;
      });
    }
  }

  // =========================================================
  // MENU
  // =========================================================
  handleMenu(ep: any) {

    const rid = this.menuRestaurantId;

    if (ep.method === 'GET' && ep.path.includes('restaurant')) {

      if (!rid) return alert("Enter Restaurant ID");

      this.http.get<any>(
        `${this.baseUrl}/menu-items/restaurant/${rid}`,
        this.authHeader()
      ).subscribe(res => {
        this.menuItems = res.data || res;
        this.showMenuGetPopup = true;
      });
    }

    if (ep.method === 'GET' && ep.path.includes('{itemId}')) {
      this.menuActionType = 'GET';
      this.menuActionTitle = "Enter Item ID";
      this.showMenuIdPopup = true;
    }

    if (ep.method === 'POST') {
      if (!rid) return alert("Enter Restaurant ID");
      this.menuActionType = 'POST';
      this.newMenu = { itemName: '', itemDescription: '', itemPrice: '' };
      this.showMenuFormPopup = true;
    }

    if (ep.method === 'PUT') {
      this.menuActionType = 'PUT';
      this.menuActionTitle = "Enter Item ID";
      this.showMenuIdPopup = true;
    }

    if (ep.method === 'DELETE') {
      this.menuActionType = 'DELETE';
      this.menuActionTitle = "Enter Item ID";
      this.showMenuIdPopup = true;
    }
  }

  submitMenuForm() {

    this.http.post(
      `${this.baseUrl}/menu-items`,
      {
        ...this.newMenu,
        restaurantId: this.menuRestaurantId
      },
      this.authHeader()
    ).subscribe(() => {
      alert("Menu Added ✅");
      this.showMenuFormPopup = false;
    });
  }

  confirmMenuAction() {

    const id = this.menuItemId;

    if (this.menuActionType === 'GET') {
      this.http.get(
        `${this.baseUrl}/menu-items/${id}`,
        this.authHeader()
      ).subscribe(res => {
        this.menuItems = [res];
        this.showMenuGetPopup = true;
        this.showMenuIdPopup = false;
      });
    }

    if (this.menuActionType === 'PUT') {
      this.http.put(
        `${this.baseUrl}/menu-items/${id}`,
        {
          ...this.newMenu,
          restaurantId: this.menuRestaurantId
        },
        this.authHeader()
      ).subscribe(() => {
        alert("Menu Updated ✅");
        this.showMenuIdPopup = false;
      });
    }

    if (this.menuActionType === 'DELETE') {
      this.http.delete(
        `${this.baseUrl}/menu-items/${id}`,
        this.authHeader()
      ).subscribe(() => {
        alert("Menu Deleted ✅");
        this.showMenuIdPopup = false;
      });
    }
  }

  // ================= STYLE =================
  getMethodClass(method: string) {
    return {
      'bg-green-600': method === 'GET',
      'bg-orange-600': method === 'POST',
      'bg-yellow-600': method === 'PUT',
      'bg-red-600': method === 'DELETE'
    };
  }
}