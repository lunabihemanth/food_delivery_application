import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-role-select',
  imports: [CommonModule],
  templateUrl: './role-select.html',
  styleUrl: './role-select.css'
})
export class RoleSelect {
  constructor(private router: Router) {}

  selectRole(role: string) {
    localStorage.setItem('role', role);
    this.router.navigate(['/login', role]);
  }
}
