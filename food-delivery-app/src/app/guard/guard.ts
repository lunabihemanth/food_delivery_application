import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const AuthGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const username = localStorage.getItem('username');

  if (username) {
    return true;   // logged in – allow access
  }

  // Not logged in – send to login with the intended URL
  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url }
  });
};