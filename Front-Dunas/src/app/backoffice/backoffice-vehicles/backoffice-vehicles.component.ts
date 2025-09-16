import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface Vehicle {
  vehicleId: number;
  model: string;
  brand: string;
  category: string;
  plateNumber: string;
  pricePerDay: number;
  status: string;
}

@Component({
  selector: 'app-backoffice-vehicles',
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice-vehicles.component.html',
  styleUrl: './backoffice-vehicles.component.css'
})
export class BackofficeVehiclesComponent implements OnInit {
  vehicles: Vehicle[] = [];
  isLoading = false;
  error = '';
  selectedStatus: { [key: number]: string } = {};
  availableStatuses = ['AVAILABLE', 'MAINTENANCE', 'RENTED', 'UNAVAILABLE'];

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadVehicles();
  }

  loadVehicles(): void {
    this.isLoading = true;
    this.error = '';

    // Get user role from localStorage or service
    const userRole = localStorage.getItem('userRole') || '';

    fetch('http://localhost:8077/rental/vehicles/backoffice/all', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })
      .then(response => {
        console.log(response);
        if (!response.ok) {
          throw new Error('Failed to load vehicles');
        }
        return response.json();
      })
      .then(data => {
        this.vehicles = data || [];
        // Initialize selected status for each vehicle
        this.vehicles.forEach(vehicle => {
          this.selectedStatus[vehicle.vehicleId] = vehicle.status;
        });
        this.isLoading = false;
      })
      .catch(error => {
        this.error = 'Failed to load vehicles';
        this.isLoading = false;
        this.vehicles = []; // Ensure empty array for @empty to work
        console.error('Error loading vehicles:', error);
      });
  }

  updateVehicleStatus(vehicleId: number): void {
    const newStatus = this.selectedStatus[vehicleId];
    const userRole = localStorage.getItem('userRole') || '';

    // Show loading state for this specific vehicle
    const updateBtn = document.querySelector(`[data-vehicle-id="${vehicleId}"]`) as HTMLButtonElement;
    if (updateBtn) {
      updateBtn.disabled = true;
      updateBtn.textContent = 'Updating...';
    }

    fetch('http://localhost:8077/rental/vehicles/backoffice/update/status', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify({
        vehicleId: vehicleId.toString(),
        status: newStatus
      })
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to update vehicle status');
        }
        // Update the vehicle status in the local array
        const vehicle = this.vehicles.find(v => v.vehicleId === vehicleId);
        if (vehicle) {
          vehicle.status = newStatus;
        }
        this.showSuccessMessage('Vehicle status updated successfully');
      })
      .catch(error => {
        this.error = 'Failed to update vehicle status';
        console.error('Error updating vehicle status:', error);
        // Reset selected status to original value
        const vehicle = this.vehicles.find(v => v.vehicleId === vehicleId);
        if (vehicle) {
          this.selectedStatus[vehicleId] = vehicle.status;
        }
      })
      .finally(() => {
        // Reset button state
        if (updateBtn) {
          updateBtn.disabled = false;
          updateBtn.textContent = 'Update';
        }
      });
  }

  goBackToBackoffice(): void {
    this.router.navigate(['/backoffice']);
  }

  getStatusBadgeClass(status: string): string {
    const statusClasses: { [key: string]: string } = {
      'AVAILABLE': 'status-available',
      'UNAVAILABLE': 'status-unavailable',
      'RENTED': 'status-rented',
      'MAINTENANCE': 'status-maintenance',
    };
    return statusClasses[status] || 'status-default';
  }

  private showSuccessMessage(message: string): void {
    // Simple success notification - you can replace with a proper toast service
    alert(message);
  }

  // Helper method to get available vehicles count
  get availableCount(): number {
    return this.vehicles.filter(v => v.status === 'AVAILABLE').length;
  }

  // Helper method to get rented vehicles count
  get rentedCount(): number {
    return this.vehicles.filter(v => v.status === 'RENTED').length;
  }

  // Helper method to get maintenance vehicles count
  get maintenanceCount(): number {
    return this.vehicles.filter(v => v.status === 'MAINTENANCE').length;
  }
}
