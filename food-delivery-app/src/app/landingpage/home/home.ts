import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  constructor(private router: Router) {}

  teamMembers = [
    { name: 'Annie Rufina C',       role: 'Customer & Address APIs',    route: '/annie' },
    { name: 'Hemanth Karthik M',    role: 'Restaurant & Menu APIs',     route: '/hemanth' },
    { name: 'Thenmozhi S',          role: 'Order & Order Item APIs',    route: '/thenmozhi' },
    { name: 'Kisol Shamilisha',     role: 'Delivery & Assignment APIs', route: '/kisol' },
    { name: 'Jeevitha E',           role: 'Coupons & Ratings APIs',     route: '/jeevitha' }
  ];

  goToMember(route: string) {
    this.router.navigate(['/login'], { queryParams: { returnUrl: route } });
  }

  goToAdmin() {
    this.router.navigate(['/login'], { queryParams: { returnUrl: '/admin' } });
  }
}