import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../service/auth.service';

interface VehicleDetails {
  vehicleId: number;
  model: string;
  brand: string;
  category: string;
  pricePerDay: number;
}

interface Comment {
  id: string;
  vehicleId: string;
  userId: string;
  rating: number;
  comment: string;
  timestamp: string;
}

@Component({
  selector: 'app-vehicle-details',
  imports: [CommonModule, FormsModule],
  templateUrl: './vehicle-details.component.html',
  styleUrl: './vehicle-details.component.css'
})
export class VehicleDetailsComponent implements OnInit {
  vehicleId: string = '';
  vehicle: VehicleDetails | null = null;
  comments: Comment[] = [];
  isLoading = true;
  isLoggedIn = false;
  canComment = false;

  // Comment form
  newComment = {
    rating: 5,
    comment: ''
  };
  isSubmittingComment = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.vehicleId = this.route.snapshot.paramMap.get('id') || '';
    this.isLoggedIn = this.authService.isLoggedIn();

    if (this.vehicleId) {
      this.loadVehicleDetails();
      this.loadComments();
      if (this.isLoggedIn) {
        this.checkIfUserCanComment();
      }
    }
  }

  loadVehicleDetails(): void {
    fetch(`http://localhost:8077/rental/vehicles/${this.vehicleId}`)
      .then(response => response.json())
      .then(data => {
        this.vehicle = data;
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error loading vehicle details:', error);
        this.isLoading = false;
      });
  }

  loadComments(): void {
    fetch(`http://localhost:8077/comments/vehicle/${this.vehicleId}`)
      .then(response => response.json())
      .then(data => {
        this.comments = data;
      })
      .catch(error => {
        console.error('Error loading comments:', error);
        this.comments = [];
      });
  }

  checkIfUserCanComment(): void {
    const userId = this.authService.getId();
    if (!userId) return;

    // Check if user has rented this vehicle
    fetch(`http://localhost:8077/rental/rentals/user`, {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(rentals => {
        this.canComment = rentals.some((rental: any) =>
          rental.vehicle.vehicleId.toString() === this.vehicleId &&
          rental.rentalStatus === 'COMPLETED'
        );
      })
      .catch(error => {
        console.error('Error checking rental history:', error);
        this.canComment = false;
      });
  }

  submitComment(): void {
    if (!this.newComment.comment.trim() || this.isSubmittingComment) return;

    this.isSubmittingComment = true;
    const userId = this.authService.getId();

    const commentData = {
      vehicleId: this.vehicleId,
      userId: userId,
      rating: this.newComment.rating,
      comment: this.newComment.comment.trim()
    };

    fetch(`http://localhost:8077/comments/add`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(commentData)
    })
      .then(response => {
        if (response.ok) {
          this.newComment = { rating: 5, comment: '' };
          this.loadComments(); // Reload comments
        } else {
          throw new Error('Failed to submit comment');
        }
      })
      .catch(error => {
        console.error('Error submitting comment:', error);
        alert('Failed to submit comment. Please try again.');
      })
      .finally(() => {
        this.isSubmittingComment = false;
      });
  }

  goToReservations(): void {
    this.router.navigate(['/reservations']);
  }

  getStarArray(rating: number): boolean[] {
    return Array(5).fill(false).map((_, i) => i < rating);
  }

  formatDate(timestamp: string): string {
    return new Date(timestamp).toLocaleDateString();
  }
}
