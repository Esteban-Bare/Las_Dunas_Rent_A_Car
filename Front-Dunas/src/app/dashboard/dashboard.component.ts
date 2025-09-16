import { Component, OnInit } from '@angular/core';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  userInfo: any = {};
  dashboardStats = {
    totalReservations: 0,
    activeRentals: 0,
    pendingPayments: 0,
    totalSpent: 0
  };
  isLoading = true;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
    this.loadDashboardStats();
  }

  // Check if user is manager or admin
  isManagerOrAdmin(): boolean {
    const role = this.authService.getRole();
    console.log(role);
    return role === 'MANAGER' || role === 'ADMIN';
  }

  // Navigate to appropriate backoffice based on role
  navigateToBackoffice(): void {
    const role = this.authService.getRole();
    if (role === 'ADMIN') {
      this.router.navigate(['/admin/backoffice']);
    } else if (role === 'MANAGER') {
      this.router.navigate(['/backoffice']);
    }
  }

  loadUserInfo() {
    fetch(`http://localhost:8077/user/client`,{
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(user => {
        this.userInfo = {
          firstName: user.firstName || 'User',
          lastName: user.lastName || '',
          email: user.email || '',
          memberSince: new Date(user.createdAt) || new Date()
        };
      })
  }

  loadDashboardStats() {
    this.isLoading = true;

    const userId= this.authService.getId();
    if (!userId) {
      this.isLoading = false;
      return;
    }

    Promise.all([
      this.fetchReservationsCount(userId),
      this.fetchActiveRentalsCount(userId),
      this.fetchPendingPaymentsCount(userId),
      this.fetchTotalSpent(userId)
    ]).then(() => {
      this.isLoading = false;
    }).catch(error => {
      console.error('Error loading dashboard stats:', error);
      this.isLoading = false;
    });
  }

  private fetchReservationsCount(userId: any) {
    return fetch(`http://localhost:8077/rental/reservations/user`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(reservations => {
        this.dashboardStats.totalReservations = reservations.length;
      })
      .catch(error => {
        console.error('Error fetching reservations:', error);
        this.dashboardStats.totalReservations = 0;
      });
  }

  private fetchActiveRentalsCount(userId: any) {
    return fetch(`http://localhost:8077/rental/rentals/user`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(rentals => {
        this.dashboardStats.activeRentals = rentals.filter((rental: any) =>
          rental.rentalStatus === 'IN_PROGRESS'
        ).length;
      })
      .catch(error => {
        console.error('Error fetching active rentals:', error);
        this.dashboardStats.activeRentals = 0;
      });
  }

  private fetchPendingPaymentsCount(userId: any) {
    return fetch(`http://localhost:8077/rental/payments/user/pending`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(pendingAmount => {
        this.dashboardStats.pendingPayments = pendingAmount;
      })
      .catch(() => {
        console.error('Error fetching pending payments');
        this.dashboardStats.pendingPayments = 0;
      });
  }

  private fetchTotalSpent(userId: any) {
    return fetch(`http://localhost:8077/rental/payments/user/total`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(totalAmount => {
        this.dashboardStats.totalSpent = totalAmount;
      })
      .catch(() => {
        console.error('Error fetching total spent');
        this.dashboardStats.totalSpent = 0;
      });
  }

  navigateToReservations() {
    this.router.navigate(['/client-reservations']);
  }

  navigateToRentals() {
    this.router.navigate(['/client-rentals']);
  }

  navigateToPendingPayments() {
    this.router.navigate(['/client-payments']);
  }

  navigateToCreateReservation() {
    this.router.navigate(['/create-reservation']);
  }
}
