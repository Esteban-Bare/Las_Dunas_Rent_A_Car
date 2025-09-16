import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { PaymentPopupComponent, PaymentItem } from '../payment-popup/payment-popup.component';

interface ReservationWithPayments {
  id: number;
  vehicleModel: string;
  startDate: string;
  endDate: string;
  totalCost: number;
  status: string;
  payments: PaymentItem[];
}

interface RentalWithPayments {
  id: number;
  vehicleModel: string;
  startDate: string;
  endDate: string;
  rentalStatus: string;
  payments: PaymentItem[];
}

@Component({
  selector: 'app-client-payments',
  standalone: true,
  imports: [CommonModule, PaymentPopupComponent],
  templateUrl: './client-payments.component.html',
  styleUrl: './client-payments.component.css'
})
export class ClientPaymentsComponent implements OnInit {
  reservationsWithPendingPayments: ReservationWithPayments[] = [];
  rentalsWithPendingPayments: RentalWithPayments[] = [];
  isLoading = true;
  showPaymentPopup = false;
  selectedPayments: PaymentItem[] = [];
  popupTitle = '';
  totalPendingAmount = 0;

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadPendingPayments();
  }

  async loadPendingPayments() {
    this.isLoading = true;

    try {
      await Promise.all([
        this.loadReservationPayments(),
        this.loadRentalPayments()
      ]);
      this.calculateTotalPending();
    } catch (error) {
      console.error('Error loading pending payments:', error);
    } finally {
      this.isLoading = false;
    }
  }

  private async loadReservationPayments() {
    try {
      // Get user reservations
      const reservationsResponse = await fetch(`http://localhost:8077/rental/reservations/user`, {
        method: 'GET',
        credentials: 'include'
      });
      const reservations = await reservationsResponse.json();

      // Get all payments for user
      const paymentsResponse = await fetch(`http://localhost:8077/rental/payments/user`, {
        method: 'GET',
        credentials: 'include'
      });
      const payments = await paymentsResponse.json();

      // Filter and group pending payments by reservation
      this.reservationsWithPendingPayments = reservations
        .map((reservation: any) => {
          const pendingPayments = payments.filter((payment: any) =>
            payment.reservation &&
            payment.reservation.id === reservation.id &&
            payment.paymentStatus === 'PENDING'
          ).map((payment: any) => ({
            id: payment.id,
            amount: payment.amount,
            paymentType: payment.paymentType,
            description: payment.description,
            status: payment.paymentStatus
          }));

          return {
            id: reservation.id,
            vehicleModel: reservation.vehicle.model,
            startDate: reservation.startDate,
            endDate: reservation.endDate,
            totalCost: reservation.totalCost,
            status: reservation.status,
            payments: pendingPayments
          };
        })
        .filter((reservation: ReservationWithPayments) => reservation.payments.length > 0);

    } catch (error) {
      console.error('Error loading reservation payments:', error);
    }
  }

  private async loadRentalPayments() {
    try {
      // Get user rentals
      const rentalsResponse = await fetch(`http://localhost:8077/rental/rentals/user`, {
        method: 'GET',
        credentials: 'include'
      });
      const rentals = await rentalsResponse.json();

      // Get all payments for user
      const paymentsResponse = await fetch(`http://localhost:8077/rental/payments/user`, {
        method: 'GET',
        credentials: 'include'
      });
      const payments = await paymentsResponse.json();

      // Filter and group pending payments by rental
      this.rentalsWithPendingPayments = rentals
        .map((rental: any) => {
          const pendingPayments = payments.filter((payment: any) =>
            payment.rental &&
            payment.rental.id === rental.id &&
            payment.paymentStatus === 'PENDING' &&
            payment.paymentType !== 'DEPOSIT_RETURN'
          ).map((payment: any) => ({
            id: payment.id,
            amount: payment.amount,
            paymentType: payment.paymentType,
            description: payment.description,
            status: payment.paymentStatus
          }));

          return {
            id: rental.id,
            vehicleModel: rental.vehicle.model,
            startDate: rental.startDate,
            endDate: rental.endDate,
            rentalStatus: rental.rentalStatus,
            payments: pendingPayments
          };
        })
        .filter((rental: RentalWithPayments) => rental.payments.length > 0);

    } catch (error) {
      console.error('Error loading rental payments:', error);
    }
  }

  private calculateTotalPending() {
    const reservationTotal = this.reservationsWithPendingPayments
      .reduce((sum, res) => sum + res.payments.reduce((pSum, p) => pSum + p.amount, 0), 0);

    const rentalTotal = this.rentalsWithPendingPayments
      .reduce((sum, rental) => sum + rental.payments.reduce((pSum, p) => pSum + p.amount, 0), 0);

    this.totalPendingAmount = reservationTotal + rentalTotal;
  }

  payReservationPayments(reservation: ReservationWithPayments) {
    this.selectedPayments = reservation.payments;
    this.popupTitle = `Pay for Reservation - ${reservation.vehicleModel}`;
    this.showPaymentPopup = true;
  }

  payRentalPayments(rental: RentalWithPayments) {
    this.selectedPayments = rental.payments;
    this.popupTitle = `Pay for Rental - ${rental.vehicleModel}`;
    this.showPaymentPopup = true;
  }

  payAllPendingPayments() {
    const allPayments = [
      ...this.reservationsWithPendingPayments.flatMap(res => res.payments),
      ...this.rentalsWithPendingPayments.flatMap(rental => rental.payments)
    ];

    this.selectedPayments = allPayments;
    this.popupTitle = 'Pay All Pending Payments';
    this.showPaymentPopup = true;
  }

  onPaymentCompleted() {
    this.showPaymentPopup = false;
    alert('Payments processed successfully!');
    this.loadPendingPayments(); // Refresh the data
  }

  onPaymentCancelled() {
    this.showPaymentPopup = false;
  }

  getPaymentTypeClass(paymentType: string): string {
    switch (paymentType) {
      case 'RESERVATION': return 'payment-reservation';
      case 'RENTAL': return 'payment-rental';
      case 'INSURANCE': return 'payment-insurance';
      case 'DEPOSIT': return 'payment-deposit';
      case 'FINE': return 'payment-fine';
      default: return 'payment-default';
    }
  }

  getReservationTotal(reservation: ReservationWithPayments): number {
    return reservation.payments.reduce((sum, payment) => sum + payment.amount, 0);
  }

  getRentalTotal(rental: RentalWithPayments): number {
    return rental.payments.reduce((sum, payment) => sum + payment.amount, 0);
  }

  goBackToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
