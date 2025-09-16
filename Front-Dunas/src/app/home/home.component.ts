import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  featuredVehicles = [
    { brand: 'Toyota', model: 'Camry', category: 'Sedan', pricePerDay: 45 },
    { brand: 'Honda', model: 'CR-V', category: 'SUV', pricePerDay: 55 },
    { brand: 'BMW', model: 'Serie 3', category: 'Luxury', pricePerDay: 75 }
  ];

  services = [
    {
      icon: 'bi-car-front',
      title: 'Large Fleet',
      description: 'Wide selection of vehicles for every need'
    },
    {
      icon: 'bi-shield-check',
      title: 'Full Insurance',
      description: 'Complete coverage for peace of mind'
    },
    {
      icon: 'bi-clock',
      title: '24/7 Support',
      description: 'Around the clock customer assistance'
    },
    {
      icon: 'bi-geo-alt',
      title: 'Multiple Locations',
      description: 'Convenient pickup and drop-off points'
    }
  ];

  constructor(public authService: AuthService) {}

  ngOnInit() {}
}
