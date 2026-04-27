import { Routes } from '@angular/router';
import { HomeComponent } from './landingpage/home/home';
import { LoginComponent } from './landingpage/login/login';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  // Member dashboards (lazy or direct – use standalone components)
  {
    path: 'annie',
    loadComponent: () => import('./annie/annie').then(m => m.Annie)
  },
  {
    path: 'hemanth',
    loadComponent: () => import('./hemanth/hemanth').then(m => m.Hemanth)
  },
  {
    path: 'thenmozhi',
    loadComponent: () => import('./thenmozhi/thenmozhi').then(m => m.Thenmozhi)
  },
  {
    path: 'kisol',
    loadComponent: () => import('./kisol/kisol').then(m => m.Kisol)
  },
  {
    path: 'jeevitha',
    loadComponent: () => import('./jeevitha/jeevitha').then(m => m.Jeevitha)
  },
  {
    path: 'admin',
    loadComponent: () => import('./admin/admin').then(m => m.Admin)
  },
  { path: '**', redirectTo: '' }   // fallback
];