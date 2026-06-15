import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  isLoginPage = false;

  constructor(public auth: AuthService, private router: Router) {
    this.router.events.subscribe(evt => {
      if (evt instanceof NavigationEnd) {
        this.isLoginPage = evt.urlAfterRedirects.startsWith('/login');
      }
    });
  }
}
