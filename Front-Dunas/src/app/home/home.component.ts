import { Component } from '@angular/core';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  myVar: string = "Hello World";

  constructor(public AuthService: AuthService) {}
  // ngOnInit() {
  //   this.AuthService.login(this.AuthService.tempCreds)
  //     .then((userData) => {
  //       console.log("User Info:", userData);
  //     })
  //     .catch((error) => {
  //       console.error("Login error:", error);
  //     });
  // }
}
