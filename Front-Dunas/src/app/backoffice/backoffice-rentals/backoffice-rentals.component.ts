import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface RentalDto {
  id: number;
  status: string;
  startDate: string;
  endDate: string;
  totalPrice: number;
  createdAt: string;
  updatedAt: string;
  vehicle: {
    vehicleId: number;
    brand: string;
    model: string;
    plateNumber: string;
    pricePerDay: number;
  };
  store: {
    storeId: number;
    name: string;
    address: string;
  };
  reservation?: {
    id: number;
  };
}

@Component({
  selector: 'app-backoffice-rentals',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './backoffice-rentals.component.html',
  styleUrl: './backoffice-rentals.component.css'
})
export class BackofficeRentalsComponent implements OnInit {
  rentals: RentalDto[] = [];
  isLoading = false;
  error: string | null = null;
  completingRentalId: number | null = null;

  // Stats
  get totalRentals(): number {
    return this.rentals.length;
  }

  get inProgressCount(): number {
    return this.rentals.filter(r => r.status === 'IN_PROGRESS').length;
  }

  get completedCount(): number {
    return this.rentals.filter(r => r.status === 'COMPLETED').length;
  }

  get overdueCount(): number {
    return this.rentals.filter(r => r.status === 'OVERDUE').length;
  }

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadRentals();
  }

  async loadRentals() {
    this.isLoading = true;
    this.error = null;

    try {
      const response = await fetch('http://localhost:8077/rental/rentals/all', {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error('Failed to load rentals');
      }

      this.rentals = await response.json();
    } catch (error) {
      console.error('Error loading rentals:', error);
      this.error = 'Failed to load rentals. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  async completeRental(rentalId: number) {
    this.completingRentalId = rentalId;

    try {
      const response = await fetch(`http://localhost:8077/rental/rentals/${rentalId}/complete`, {
        method: 'PUT',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorMessage = await response.text();
        throw new Error(errorMessage || 'Failed to complete rental');
      }

      // Reload rentals to update the status
      await this.loadRentals();
    } catch (error) {
      console.error('Error completing rental:', error);
      this.error = error instanceof Error ? error.message : 'Failed to complete rental. Please try again.';
    } finally {
      this.completingRentalId = null;
    }
  }

  canCompleteRental(status: string): boolean {
    return status === 'IN_PROGRESS';
  }

  goBackToBackoffice() {
    this.router.navigate(['/backoffice']);
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'IN_PROGRESS': 'status-in-progress',
      'COMPLETED': 'status-completed',
      'CANCELED': 'status-canceled',
      'RETURNED': 'status-returned',
      'OVERDUE': 'status-overdue'
    };
    return statusMap[status] || 'status-default';
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }
}
