import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-my-orders',
  imports: [CommonModule, RouterLink],
  templateUrl: './my-orders.html',
  styleUrl: './my-orders.css'
})
export class MyOrders implements OnInit {

  orders: any[] = [];
  statuses = ['All', 'Pending', 'Out for Delivery', 'Delivered', 'Cancelled'];
  selectedStatus = 'All';

  constructor(private router: Router) {}

  ngOnInit() {
    this.orders = [
      { orderId: 1001, restaurantName: 'Tasty Bites', orderStatus: 'Delivered', orderDate: '2024-01-01 12:00', totalAmount: 450, items: 3 },
      { orderId: 1002, restaurantName: 'Pizza Palace', orderStatus: 'Out for Delivery', orderDate: '2024-01-02 13:00', totalAmount: 320, items: 2 },
      { orderId: 1003, restaurantName: 'Burger Barn', orderStatus: 'Pending', orderDate: '2024-01-03 14:00', totalAmount: 210, items: 1 },
      { orderId: 1004, restaurantName: 'Spice Avenue', orderStatus: 'Cancelled', orderDate: '2024-01-04 15:00', totalAmount: 180, items: 2 },
    ];
  }

  filteredOrders() {
    if (this.selectedStatus === 'All') return this.orders;
    return this.orders.filter(o => o.orderStatus === this.selectedStatus);
  }

  getStatusClass(status: string) {
    if (status === 'Pending') return 'bg-yellow-900 text-yellow-300';
    if (status === 'Out for Delivery') return 'bg-blue-900 text-blue-300';
    if (status === 'Delivered') return 'bg-green-900 text-green-300';
    if (status === 'Cancelled') return 'bg-red-900 text-red-300';
    return 'bg-gray-700 text-gray-300';
  }

  viewDetails(order: any) {
    localStorage.setItem('selectedOrder', JSON.stringify(order));
    this.router.navigate(['/customer/orders']);
  }

  rateOrder(event: any, order: any) {
    event.stopPropagation();
    localStorage.setItem('selectedOrder', JSON.stringify(order));
    this.router.navigate(['/customer/rating', order.orderId]);
  }
}