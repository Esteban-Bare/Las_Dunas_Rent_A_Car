import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../service/auth.service';
import {Router} from '@angular/router';

interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  createdAt: string;
}

@Component({
  selector: 'app-backoffice-users',
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice-users.component.html',
  styleUrl: './backoffice-users.component.css'
})
export class BackofficeUsersComponent implements OnInit {
  users: User[] = [];
  isLoading = true;
  availableRoles = ['CLIENT', 'ADMIN', 'MANAGER'];

  constructor(private authService: AuthService,private router: Router) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  goBackToBackoffice(): void {
    this.router.navigate(['/admin/backoffice']);
  }

  loadUsers(): void {
    this.isLoading = true;
    fetch('http://localhost:8077/user/all', {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(users => {
        this.users = users;
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error loading users:', error);
        this.isLoading = false;
      });
  }

  updateUserRole(userId: number, newRole: string): void {
    const updateData = {
      id: userId.toString(),
      role: newRole
    };

    fetch('http://localhost:8077/user/update/role', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include',
      body: JSON.stringify(updateData)
    })
      .then(response => {
        if (response.ok) {
          // Update the user role in the local array
          const user = this.users.find(u => u.id === userId);
          if (user) {
            user.role = newRole;
          }
          alert('User role updated successfully');
        } else {
          alert('Failed to update user role');
        }
      })
      .catch(error => {
        console.error('Error updating user role:', error);
        alert('Error updating user role');
      });
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
