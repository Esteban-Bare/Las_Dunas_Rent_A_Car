import {Component, OnInit} from '@angular/core';
import {AuthService} from '../service/auth.service';
import {AsyncPipe} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {RouterLink} from '@angular/router';

interface VehicleInfo {
  model: string;
  brand: string;
  category: string;
  pricePerDay: number;
  priceDto: {
    reservationPrice: number;
    insuranceRefundPrice: number;
    realRentalPrice: number;
  };
}

interface LocationVehicles {
  location: string;
  vehicles: VehicleInfo[];
}

@Component({
  selector: 'app-reservation',
  imports: [
    AsyncPipe,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './reservation.component.html',
  styleUrl: './reservation.component.css'
})
export class ReservationComponent implements OnInit{
  availableVehiclesByLocation: LocationVehicles[] = [];
  hasSearched: boolean = false;

  constructor(protected authService: AuthService) {}
  ngOnInit() {
    this.authService.checkAuthStatus();
  }


  availableCarsForm: FormGroup = new FormGroup({
    city: new FormControl<string>("", [
        Validators.required,
        Validators.pattern('^(toulon|marseille|nice|paris)$')
      ]),
    startDate: new FormControl<string>("", [
       Validators.required
    ]),
    endDate: new FormControl<string>("", [
      Validators.required
    ]),
  })

  searchAvailableCars() {
    if (this.availableCarsForm.valid) {
      const searchData = {
        city: this.availableCarsForm.get('city')?.value,
        startDateHour: this.availableCarsForm.get('startDate')?.value  + ":00",
        endDateHour: this.availableCarsForm.get('endDate')?.value  + ":00",
      };

      console.log(searchData);
      fetch("http://localhost:8077/rental/vehicle/available", {
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(searchData),
        credentials: 'include'
      })
        .then((response) => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error('Network response was not ok');
          }
        })
        .then((data) => {
          // Transform the response to match the expected format
          const transformedData: LocationVehicles[] = [];

          if (data.vehicles) {
            Object.keys(data.vehicles).forEach(locationName => {
              transformedData.push({
                location: locationName,
                vehicles: data.vehicles[locationName]
              });
            });
          }

          this.availableVehiclesByLocation = transformedData;
          this.hasSearched = true;
          console.log('Available vehicles:', this.availableVehiclesByLocation);
        })
        .catch((error) => {
          console.error('There has been a problem with your fetch operation:', error);
        });
    }
  }

  protected readonly location = location;
}
