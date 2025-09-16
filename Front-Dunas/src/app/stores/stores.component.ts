import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Store {
  id: number;
  name: string;
  address: string;
  phone: string;
  city: string;
}

@Component({
  selector: 'app-stores',
  imports: [CommonModule],
  templateUrl: './stores.component.html',
  styleUrl: './stores.component.css'
})
export class StoresComponent implements OnInit {
  stores: Store[] = [];
  isLoading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.loadStores();
  }

  loadStores() {
    fetch('http://localhost:8077/rental/stores/all', {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Failed to fetch stores');
        }
        return response.json();
      })
      .then(data => {
        this.stores = data;
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error loading stores:', error);
        this.error = 'Failed to load stores';
        this.isLoading = false;
      });
  }
}
