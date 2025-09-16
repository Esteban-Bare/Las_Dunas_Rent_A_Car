import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-backoffice',
  imports: [CommonModule],
  templateUrl: './backoffice-index.component.html',
  styleUrl: './backoffice-index.component.css'
})
export class BackofficeIndexComponent implements OnInit {
  isLoading = true;
  userRole: string | null = null;
  isAdmin = false;
  isManager = false;

  // Statistics
  systemStats = {
    totalUsers: 0,
    totalVehicles: 0,
    totalActiveRentals: 0,
    totalRevenue: 0,
    activeRentals: 0,
    pendingPayments: 0
  };

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userRole = this.authService.getRole();
    this.isAdmin = this.userRole === 'ADMIN';
    this.isManager = this.userRole === 'MANAGER';
    this.loadBackofficeData();
  }

  loadBackofficeData(): void {
    this.isLoading = true;

    Promise.all([
      this.loadSystemStats(),
    ]).then(() => {
      this.isLoading = false;
    }).catch(error => {
      console.error('Error loading backoffice data:', error);
      this.isLoading = false;
    });
  }

  private async loadSystemStats(): Promise<void> {

    try {
      const endpoints = [
        { key: 'totalVehicles', url: `http://localhost:8077/rental/vehicles/common/count` },
        { key: 'totalActiveRentals', url: `http://localhost:8077/rental/rentals/active/count` },
        { key: 'totalRevenue', url: `http://localhost:8077/rental/payments/total-revenue` }
      ];

      // Add user count endpoint only for admins
      if (this.isAdmin) {
        endpoints.push({ key: 'totalUsers', url: `http://localhost:8077/user/count` });
      }

      for (const endpoint of endpoints) {
        try {
          const response = await fetch(endpoint.url, {
            method: 'GET',
            credentials: 'include'
          });
          if (response.ok) {
            this.systemStats[endpoint.key as keyof typeof this.systemStats] = await response.json();
          }
        } catch (error) {
          console.error(`Error loading ${endpoint.key}:`, error);
        }
      }
    } catch (error) {
      console.error('Error loading system stats:', error);
    }
  }

  // Navigation methods with role-based routing
  navigateToUserManagement(): void {
    if (this.isAdmin) {
      this.router.navigate(['/admin/backoffice/users']);
    } else {
      // Managers might have limited user access or no access
      console.log('User management not available for managers');
    }
  }

  navigateToVehicleManagement(): void {
    this.router.navigate(['/backoffice/vehicles']);
  }

  navigateToReservationManagement(): void {
    this.router.navigate(['/backoffice/reservations']);
  }

  navigateToPaymentManagement(): void {
    this.router.navigate(["/backoffice/payments"]);
  }


  goBackToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToRentalManagement(): void {
    this.router.navigate(['/backoffice/rentals']);
  }
}
