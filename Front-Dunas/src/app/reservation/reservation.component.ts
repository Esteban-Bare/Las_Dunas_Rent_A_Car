import {Component, OnInit} from '@angular/core';
import {AuthService} from '../service/auth.service';
import {AsyncPipe} from '@angular/common';
import {FormControl, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';

interface VehicleInfo {
  vehicleId: number;
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

interface StoreData {
  storeId: number;
  location: string;
  vehicles: VehicleInfo[];
}

interface AvailableVehiclesResponse {
  vehiclesPerCity: {
    storeId: number;
    storeVehicles: { [location: string]: VehicleInfo[] };
  }[];
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
  availableStores: StoreData[] = [];
  hasSearched: boolean = false;

  constructor(protected authService: AuthService, private router: Router) {}

  ngOnInit() {
    this.authService.checkAuthStatus();
  }

  dateRangeValidator(group: AbstractControl): ValidationErrors | null {
    const startDate = group.get('startDate')?.value;
    const endDate = group.get('endDate')?.value;

    if (startDate && endDate && new Date(startDate) >= new Date(endDate)) {
      return { dateRange: true };
    }
    return null;
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
      Validators.required,
    ]),
  }, { validators: this.dateRangeValidator })

  searchAvailableCars() {
    if (this.availableCarsForm.valid) {
      const searchData = {
        city: this.availableCarsForm.get('city')?.value,
        startDateHour: this.availableCarsForm.get('startDate')?.value  + ":00",
        endDateHour: this.availableCarsForm.get('endDate')?.value  + ":00",
      };

      console.log(searchData);
      fetch("http://localhost:8077/rental/vehicles/common/available", {
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
        .then((data: AvailableVehiclesResponse) => {
          const stores: StoreData[] = [];

          data.vehiclesPerCity.forEach(store => {
            Object.entries(store.storeVehicles).forEach(([location, vehicles]) => {
              stores.push({
                storeId: store.storeId,
                location: location,
                vehicles: vehicles
              });
            });
          });

          this.availableStores = stores;
          this.hasSearched = true;
          console.log('Available vehicles:', this.availableStores);
        })
        .catch((error) => {
          console.error('There has been a problem with your fetch operation:', error);
        });
    }
  }

  selectVehicle(vehicle: VehicleInfo, store: StoreData) {
    const reservationDetails = {
      vehicle: vehicle,
      store: store,
      startDate: this.availableCarsForm.get('startDate')?.value + ":00",
      endDate: this.availableCarsForm.get('endDate')?.value + ":00",
    }

    this.router.navigate(['/create-reservation'], { state: {reservationDetails: reservationDetails} });
  }

  protected readonly location = location;
}
