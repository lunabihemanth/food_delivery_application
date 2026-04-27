import { Component } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMsg = '';
  isLoading = false;
  returnUrl = '/home';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private http: HttpClient
  ) {
    this.route.queryParams.subscribe(params => {
      this.returnUrl = params['returnUrl'] || '/home';
    });
  }

  goBack() {
    this.router.navigate(['/home']);
  }

  onLogin() {
    if (!this.username || !this.password) {
      this.errorMsg = 'Please enter username and password';
      return;
    }

    this.isLoading = true;
    this.errorMsg = '';

    const credentials = btoa(`${this.username}:${this.password}`);
    const headers = new HttpHeaders({ Authorization: 'Basic ' + credentials });

    // Test against a protected endpoint that every authenticated user can reach
    this.http.get('http://localhost:8081/orders', {
      headers,
      observe: 'response'
    }).subscribe({
      next: () => {
        this.loginSuccess(credentials);
      },
      error: (err) => {
        this.isLoading = false;
        if (err.status === 401) {
          this.errorMsg = 'Invalid username or password';
        } else if (err.status === 403) {
          // Correct credentials, but user doesn't have access to /orders – still valid login
          this.loginSuccess(credentials);
        } else if (err.status === 0) {
          this.errorMsg = 'Cannot connect to server. Is the backend running?';
        } else {
          this.errorMsg = 'Server error. Please try again.';
        }
      }
    });
  }

  private loginSuccess(credentials: string) {
    localStorage.setItem('authHeader', credentials);
    localStorage.setItem('username', this.username);
    this.router.navigate([this.returnUrl]);
  }
}