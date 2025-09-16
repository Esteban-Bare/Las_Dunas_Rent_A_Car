import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

interface Reservation {
  id: number;
  vehicleId: number;
  vehicle: {
    brand: string;
    model: string;
    category: string;
    pricePerDay: number;
  };
  store: {
    address: string;
  };
  requested_start_date: string;
  requested_end_date: string;
  status: string;
  reservationPrice: string;
  insuranceRefundPrice: string;
  createdAt: string;
  payments: Payment[];
  hasRental: boolean;
}

interface Payment {
  id: number;
  paymentStatus: string;
  paymentType: string;
  amount: number;
}

@Component({
  selector: 'app-client-reservations',
  imports: [CommonModule],
  templateUrl: './client-reservations.component.html',
  styleUrl: './client-reservations.component.css'
})
export class ClientReservationsComponent implements OnInit {
  reservations: Reservation[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadReservations();
  }

  loadReservations() {
    this.isLoading = true;

    fetch(`http://localhost:8077/rental/reservations/user`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch reservations');
        }
        return response.json();
      })
      .then(data => {
        console.log('Fetched reservations:', data);
        this.reservations = data;
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error loading reservations:', error);
        this.error = 'Failed to load reservations';
        this.isLoading = false;
      });
  }

  cancelReservation(id: number) {
    if (!confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }

    fetch(`http://localhost:8077/rental/reservations/${id}/cancel`, {
      method: 'PUT',
      credentials: 'include'
    })
      .then(response => {
        console.log(response)
        if (!response.ok) {
          throw new Error('Failed to cancel reservation');
        }
      })
      .then(() => {
        alert('Reservation cancelled successfully');
        this.loadReservations(); // Reload the list
      })
      .catch(error => {
        console.error('Error cancelling reservation:', error);
        alert('Failed to cancel reservation. Please try again.');
      });
  }

  getPaymentStatus(reservation: Reservation): string {
    if (!reservation.payments || reservation.payments.length === 0) {
      return 'NO_PAYMENTS';
    }

    const hasPending = reservation.payments.some(p => p.paymentStatus === 'PENDING');
    const hasCompleted = reservation.payments.some(p => p.paymentStatus === 'COMPLETED');

    if (hasPending && hasCompleted) {
      return 'PARTIALLY_PAID';
    } else if (hasCompleted) {
      return 'FULLY_PAID';
    } else if (hasPending) {
      return 'PENDING_PAYMENT';
    } else {
      return 'UNPAID';
    }
  }

  getStatusClass(status: string, reservation :Reservation): string {
    if (status === 'COMPLETED' && this.getPaymentStatus(reservation) === 'PENDING_PAYMENT') {
      reservation.status = 'PENDING';
      return 'status-pending';
    } else if (status === 'COMPLETED') {
      return 'status-completed';
    } else if (status === 'CANCELLED') {
      return 'status-cancelled';
    } else {
      return 'status-default';
    }
  }

  getPaymentStatusClass(status: string): string {
    switch (status) {
      case 'FULLY_PAID': return 'payment-paid';
      case 'PARTIALLY_PAID': return 'payment-partial';
      case 'PENDING_PAYMENT': return 'payment-pending';
      case 'UNPAID': return 'payment-unpaid';
      case 'NO_PAYMENTS': return 'payment-none';
      default: return 'payment-default';
    }
  }

  getTotalPaid(reservation: Reservation): number {
    return reservation.payments
      .filter( p => p.paymentType === 'RESERVATION' || p.paymentType === 'INSURANCE')
      .reduce((sum, p) => sum + p.amount, 0);
  }

  canCancelReservation(reservation: Reservation): boolean {
    if (reservation.status === 'CANCELLED') {
      return false;
    }
    return reservation.insuranceRefundPrice !== '0.00' || reservation.status === 'PENDING';
  }

  navigateToCreateReservation() {
    this.router.navigate(['/create-reservation']);
  }

  goBackToDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
