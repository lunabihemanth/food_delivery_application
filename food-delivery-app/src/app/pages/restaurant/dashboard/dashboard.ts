import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  restaurantName = 'Tasty Bites';
  stats = {
    totalOrders: 0,
    pendingOrders: 0,
    deliveredOrders: 0,
    totalRatings: 0,
    avgRating: 0
  };

  recentOrders: any[] = [];

  constructor(private router: Router) {}

  ngOnInit() {
    this.stats = {
      totalOrders: 50,
      pendingOrders: 8,
      deliveredOrders: 38,
      totalRatings: 42,
      avgRating: 4.5
    };

    this.recentOrders = [
      { orderId: 1001, customerName: 'John Smith', orderStatus: 'Pending', totalAmount: 450 },
      { orderId: 1002, customerName: 'Alice Johnson', orderStatus: 'Delivered', totalAmount: 320 },
      { orderId: 1003, customerName: 'Michael Brown', orderStatus: 'Pending', totalAmount: 210 },
    ];
  }

  getStatusClass(status: string) {
    if (status === 'Pending') return 'bg-yellow-900 text-yellow-300';
    if (status === 'Delivered') return 'bg-green-900 text-green-300';
    return 'bg-gray-700 text-gray-300';
  }

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      localStorage.clear();
      this.router.navigate(['/welcome']);
    }
  }
}


