import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-menu',
  imports: [CommonModule, FormsModule],
  templateUrl: './menu.html',
  styleUrl: './menu.css'
})
export class Menu implements OnInit {

  restaurant: any = null;
  menuItems: any[] = [];
  cart: any[] = [];
  search = '';

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    this.restaurant = JSON.parse(localStorage.getItem('selectedRestaurant') || '{}');
    this.cart = JSON.parse(localStorage.getItem('cart') || '[]');
    
    
    // Dummy data — will replace with API later
    this.menuItems = [
      { itemId: 1, itemName: 'Margherita Pizza', description: 'Classic pizza with tomato and mozzarella', price: 299 },
      { itemId: 2, itemName: 'Pepperoni Pizza', description: 'Spicy pepperoni with cheese', price: 349 },
      { itemId: 3, itemName: 'Chicken Burger', description: 'Grilled chicken with lettuce and mayo', price: 199 },
      { itemId: 4, itemName: 'Veggie Wrap', description: 'Fresh vegetables in soft tortilla', price: 149 },
      { itemId: 5, itemName: 'Caesar Salad', description: 'Romaine lettuce with croutons and dressing', price: 179 },
      { itemId: 6, itemName: 'Garlic Bread', description: 'Toasted bread with garlic butter', price: 99 },
    ];
  }

  filteredItems() {
    return this.menuItems.filter(i =>
      i.itemName.toLowerCase().includes(this.search.toLowerCase())
    );
  }

  getCartItem(itemId: number) {
    return this.cart.find(c => c.itemId === itemId);
  }

  addToCart(item: any) {
    const existing = this.cart.find(c => c.itemId === item.itemId);
    if (existing) {
      existing.quantity++;
    } else {
      this.cart.push({ ...item, quantity: 1 });
    }
    this.saveCart();
  }

  increaseQty(item: any) {
    const existing = this.cart.find(c => c.itemId === item.itemId);
    if (existing) existing.quantity++;
    this.saveCart();
  }

  decreaseQty(item: any) {
    const existing = this.cart.find(c => c.itemId === item.itemId);
    if (existing) {
      existing.quantity--;
      if (existing.quantity === 0) {
        this.cart = this.cart.filter(c => c.itemId !== item.itemId);
      }
    }
    this.saveCart();
  }

  cartCount() {
    return this.cart.reduce((s, c) => s + c.quantity, 0);
  }

  cartTotal() {
    return this.cart.reduce((s, c) => s + (c.price * c.quantity), 0);
  }

  saveCart() {
    localStorage.setItem('cart', JSON.stringify(this.cart));
  }

  goBack() {
    this.router.navigate(['/customer/home']);
  }

  goToCart() {
    this.router.navigate(['/customer/cart']);
  }
}