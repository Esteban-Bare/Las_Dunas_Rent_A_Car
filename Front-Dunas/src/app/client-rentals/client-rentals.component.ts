import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface RentalDto {
  id: number;
  userId: number;
  vehicle: {
    id: number;
    brand: string;
    model: string;
    category: string;
    pricePerDay: number;
  };
  store: {
    id: number;
    name: string;
    address: string;
    location: string;
  };
  reservation: {
    id: number;
    status: string;
  };
  startDate: string;
  endDate: string;
  status: string;
  totalPrice: string;
  createdAt: string;
  updatedAt: string;
  payment?: {
    id: number;
    amount: string;
    paymentType: string;
    paymentStatus: string;
  };
}

@Component({
  selector: 'app-client-rentals',
  imports: [CommonModule],
  templateUrl: './client-rentals.component.html',
  styleUrl: './client-rentals.component.css'
})
export class ClientRentalsComponent implements OnInit {
  rentals: RentalDto[] = [];
  isLoading = false;
  error: string | null = null;

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadRentals();
  }

  async loadRentals() {
    this.isLoading = true;
    this.error = null;

    try {
      const response = await fetch('http://localhost:8077/rental/rentals/user', {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to fetch rentals');
      }

      this.rentals = await response.json();
    } catch (error) {
      console.error('Error loading rentals:', error);
      this.error = 'Failed to load rentals. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  goBackToDashboard() {
    this.router.navigate(['/dashboard']);
  }

  navigateToReservations() {
    this.router.navigate(['/client-reservations']);
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'IN_PROGRESS': 'status-in-progress',
      'COMPLETED': 'status-completed',
      'CANCELLED': 'status-cancelled',
      'OVERDUE': 'status-overdue',
      'RETURNED': 'status-returned'
    };
    return statusMap[status] || 'status-default';
  }

  getPaymentStatus(rental: RentalDto): string {
    if (!rental.payment) {
      return 'none';
    }
    return rental.payment.paymentStatus.toLowerCase();
  }

  getPaymentStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'completed': 'payment-paid',
      'pending': 'payment-pending',
      'failed': 'payment-unpaid',
      'none': 'payment-none'
    };
    return statusMap[status] || 'payment-pending';
  }

  canReturnVehicle(rental: RentalDto): boolean {
    return rental.status === 'IN_PROGRESS';
  }

  canCancelRental(rental: RentalDto): boolean {
    return rental.status === 'IN_PROGRESS';
  }

  async returnVehicle(rentalId: number) {
    try {
      const response = await fetch(`http://localhost:8077/rental/rentals/${rentalId}/return`, {
        method: 'PUT',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to return vehicle');
      }

      await this.loadRentals();
    } catch (error) {
      console.error('Error returning vehicle:', error);
      this.error = 'Failed to return vehicle. Please try again.';
    }
  }

  async cancelRental(rentalId: number) {
    if (!confirm('Are you sure you want to cancel this rental?')) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8077/rental/rentals/${rentalId}/cancel`, {
        method: 'PUT',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to cancel rental');
      }

      await this.loadRentals();
    } catch (error) {
      console.error('Error canceling rental:', error);
      this.error = 'Failed to cancel rental. Please try again.';
    }
  }

  formatDate(dateString: string): Date {
    return new Date(dateString);
  }
}
