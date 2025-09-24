import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { CommonModule } from '@angular/common';
import { PaymentPopupComponent, PaymentItem } from '../payment-popup/payment-popup.component';
import {FormsModule} from '@angular/forms';

interface reservationDetails {
  vehicle: {
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
  };
  store: {
    storeId: number;
    location: string;
  };
  startDate: string;
  endDate: string;
}

@Component({
  selector: 'app-create-reservation',
  imports: [CommonModule, PaymentPopupComponent, FormsModule],
  templateUrl: './create-reservation.component.html',
  styleUrl: './create-reservation.component.css'
})
export class CreateReservationComponent implements OnInit {
  reservationDetails: reservationDetails | null = null;
  isLoading = false;
  showPaymentPopup = false;
  reservationPayments: PaymentItem[] = [];
  includeInsurance = true; // Default to include insurance

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      this.reservationDetails = navigation.extras.state['reservationDetails'];
    }
  }

  ngOnInit() {
    if (!this.reservationDetails) {
      this.router.navigate(['/reservations']);
    }
    this.authService.checkAuthStatus();
  }

  calculateTotalPrice(): number {
    if (!this.reservationDetails) return 0;

    const reservationPrice = this.reservationDetails.vehicle.priceDto.reservationPrice;
    const insurancePrice = this.includeInsurance ? this.reservationDetails.vehicle.priceDto.insuranceRefundPrice : 0;

    return reservationPrice + insurancePrice;
  }

  completeReservation() {
    if (!this.reservationDetails) return;

    this.isLoading = true;

    const newReservationDto = {
      vehicleId: this.reservationDetails.vehicle.vehicleId,
      storeId: this.reservationDetails.store.storeId,
      requestedStartDate: this.reservationDetails.startDate,
      requestedEndDate: this.reservationDetails.endDate,
      reservationPrice: this.reservationDetails.vehicle.priceDto.reservationPrice,
      insurancePrice: this.includeInsurance ? this.reservationDetails.vehicle.priceDto.insuranceRefundPrice : 0
    };

    fetch('http://localhost:8077/rental/reservations/create', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(newReservationDto),
      credentials: 'include'
    })
      .then(response => {
        if (response.ok) {
          return response.json();
        } else {
          throw new Error('Failed to create reservation');
        }
      })
      .then(data => {
        console.log('Reservation created:', data);
        this.preparePaymentPopup(data);
      })
      .catch(error => {
        console.error('Error creating reservation:', error);
        alert('Failed to create reservation. Please try again.');
      })
      .finally(() => {
        this.isLoading = false;
      });
  }

  private preparePaymentPopup(reservationData: any) {
    // Fetch the payments associated with this reservation
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(payments => {
        // Filter payments for this reservation and pending status
        const reservationPayments = payments.filter((payment: any) =>
          payment.reservation &&
          payment.reservation.id === reservationData.id &&
          payment.paymentStatus === 'PENDING'
        );

        console.log(reservationPayments);
        this.reservationPayments = reservationPayments.map((payment: any) => ({
          id: payment.id,
          amount: payment.amount,
          paymentType: payment.paymentType,
          description: payment.description,
          status: payment.paymentStatus
        }));

        this.showPaymentPopup = true;
      })
      .catch(error => {
        console.error('Error fetching payments:', error);
        // If we can't fetch payments, redirect to dashboard
        this.router.navigate(['/dashboard']);
      });
  }

  onPaymentCompleted() {
    this.showPaymentPopup = false;
    alert('Payment completed successfully!');
    this.router.navigate(['/dashboard']);
  }

  onPaymentCancelled() {
    this.showPaymentPopup = false;
    this.router.navigate(['/dashboard']);
  }

  goBack() {
    this.router.navigate(['/reservations']);
  }
}
