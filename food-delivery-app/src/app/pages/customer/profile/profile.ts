import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class Profile implements OnInit {

  user: any = {};
  form: any = {};
  addresses: any[] = [];
  showAddAddress = false;
  editMode = false;

  newAddress = {
    addressLine1: '',
    city: '',
    state: '',
    postalCode: ''
  };

  constructor(private router: Router) {}

  ngOnInit() {
    // Dummy data — will replace with API later
    this.user = {
      customerId: 1,
      customerName: 'John Smith',
      customerEmail: 'john@example.com',
      customerPhone: '9876543210'
    };
    this.form = { ...this.user };

    this.addresses = [
      {
        addressId: 1,
        addressLine1: '123 Elm Street',
        city: 'Chennai',
        state: 'Tamil Nadu',
        postalCode: '600001'
      },
      {
        addressId: 2,
        addressLine1: '456 Oak Avenue',
        city: 'Mumbai',
        state: 'Maharashtra',
        postalCode: '400001'
      }
    ];
  }

  updateProfile() {
    this.user = { ...this.form };
    this.editMode = false;
    alert('Profile updated successfully!');
  }

  addAddress() {
    if (!this.newAddress.addressLine1 || !this.newAddress.city) {
      alert('Please fill all address fields');
      return;
    }
    this.addresses.push({
      ...this.newAddress,
      addressId: Date.now()
    });
    this.showAddAddress = false;
    this.newAddress = {
      addressLine1: '',
      city: '',
      state: '',
      postalCode: ''
    };
    alert('Address added successfully!');
  }

  deleteAddress(id: number) {
    if (confirm('Delete this address?')) {
      this.addresses = this.addresses.filter(a => a.addressId !== id);
    }
  }

  logout() {
    if (confirm('Are you sure you want to logout?')) {
      localStorage.clear();
      this.router.navigate(['/welcome']);
    }
  }
}
