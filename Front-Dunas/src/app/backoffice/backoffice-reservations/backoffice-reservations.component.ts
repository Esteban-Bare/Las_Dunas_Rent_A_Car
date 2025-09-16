import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface AllReservationsAdminDto {
  id: number;
  firstName: string;
  lastName: string;
  vehicleModel: string;
  vehiclePlate: string;
  paymentStatuses: string[];
  requestedStartDate: string;
  requestedEndDate: string;
  reservationStatus: string;
  totalPrice: number;
  hasRental: boolean;
}

@Component({
  selector: 'app-backoffice-reservations',
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice-reservations.component.html',
  styleUrl: './backoffice-reservations.component.css'
})
export class BackofficeReservationsComponent implements OnInit {
  reservations: AllReservationsAdminDto[] = [];
  filteredReservations: AllReservationsAdminDto[] = [];
  isLoading = false;
  error: string | null = null;
  showTodayOnly = false;

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadReservations();
  }

  async loadReservations() {
    this.isLoading = true;
    this.error = null;

    try {
      const token = localStorage.getItem('authToken');
      const userRole = localStorage.getItem('userRole');

      const response = await fetch('http://localhost:8077/rental/reservations/admin/all', {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to load reservations');
      }

      this.reservations = await response.json();
      this.applyFilter();
    } catch (error) {
      this.error = 'Failed to load reservations. Please try again.';
      console.error('Error loading reservations:', error);
    } finally {
      this.isLoading = false;
    }
  }

  applyFilter() {
    if (this.showTodayOnly) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);

      this.filteredReservations = this.reservations.filter(reservation => {
        const startDate = new Date(reservation.requestedStartDate);
        startDate.setHours(0, 0, 0, 0);
        return startDate.getTime() === today.getTime();
      });
    } else {
      this.filteredReservations = [...this.reservations];
    }
  }

  onFilterChange() {
    this.applyFilter();
  }

  canCreateRental(reservation: AllReservationsAdminDto): boolean {
    return !reservation.hasRental &&
      reservation.paymentStatuses.every(status => status === 'COMPLETED') &&
      reservation.reservationStatus === 'COMPLETED';
  }

  async createRental(reservationId: number) {
    if (!confirm('Are you sure you want to create a rental from this reservation?')) {
      return;
    }

    try {
      const token = localStorage.getItem('authToken');

      const response = await fetch(`http://localhost:8077/rental/rentals/create/${reservationId}`, {
        method: 'POST',
        credentials: 'include'
      });

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || 'Failed to create rental');
      }

      alert('Rental created successfully!');
      this.loadReservations(); // Reload to update hasRental status
    } catch (error) {
      alert(`Error creating rental: ${error}`);
      console.error('Error creating rental:', error);
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'completed':
        return 'status-completed';
      case 'cancelled':
        return 'status-cancelled';
      default:
        return 'status-default';
    }
  }

  getPaymentStatusClass(statuses: string[]): string {
    if (statuses.every(status => status === 'COMPLETED')) {
      return 'payment-all-completed';
    } else if (statuses.some(status => status === 'COMPLETED')) {
      return 'payment-partial';
    } else {
      return 'payment-pending';
    }
  }

  getPaymentStatusText(statuses: string[]): string {
    if (statuses.every(status => status === 'COMPLETED')) {
      return 'All Completed';
    } else if (statuses.some(status => status === 'COMPLETED')) {
      return 'Partially Completed';
    } else {
      return 'Pending';
    }
  }

  goBackToBackoffice() {
    this.router.navigate(['/backoffice']);
  }

  get todayReservationsCount(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    return this.reservations.filter(reservation => {
      const startDate = new Date(reservation.requestedStartDate);
      startDate.setHours(0, 0, 0, 0);
      return startDate.getTime() === today.getTime();
    }).length;
  }

  get completedReservationsCount(): number {
    return this.reservations.filter(r => r.reservationStatus === 'COMPLETED').length;
  }

  get cancelledReservationsCount(): number {
    return this.reservations.filter(r => r.reservationStatus === 'CANCELLED').length;
  }

  get rentalsCreatedCount(): number {
    return this.reservations.filter(r => r.hasRental).length;
  }
}
