import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface Vehicle {
  vehicleId: number;
  model: string;
  brand: string;
  category: string;
  pricePerDay: number;
}

@Component({
  selector: 'app-vehicles',
  imports: [CommonModule, FormsModule],
  templateUrl: './vehicles.component.html',
  styleUrl: './vehicles.component.css'
})
export class VehiclesComponent implements OnInit {
  vehicles: Vehicle[] = [];
  filteredVehicles: Vehicle[] = [];
  uniqueVehicles: Vehicle[] = [];

  categories: string[] = [];
  brands: string[] = [];

  selectedCategory: string = '';
  selectedBrand: string = '';

  isLoading = true;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.loadVehicles();
    this.loadFilters();
  }

  loadVehicles() {
    fetch('http://localhost:8077/rental/vehicles/common/all', {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(data => {
        this.vehicles = data;
        this.processUniqueVehicles();
        this.filteredVehicles = [...this.uniqueVehicles];
        this.isLoading = false;
      })
      .catch(error => {
        console.error('Error loading vehicles:', error);
        this.isLoading = false;
      });
  }

  loadFilters() {
    // Load categories
    fetch('http://localhost:8077/rental/categories/names', {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(data => {
        this.categories = data;
      })
      .catch(error => console.error('Error loading categories:', error));

    // Load brands
    fetch('http://localhost:8077/rental/brands/names', {
      method: 'GET',
      credentials: 'include'
    })
      .then(response => response.json())
      .then(data => {
        this.brands = data;
      })
      .catch(error => console.error('Error loading brands:', error));
  }

  processUniqueVehicles() {
    const uniqueMap = new Map<string, Vehicle>();

    this.vehicles.forEach(vehicle => {
      const key = `${vehicle.brand}-${vehicle.model}`;
      if (!uniqueMap.has(key)) {
        uniqueMap.set(key, vehicle);
      }
    });

    this.uniqueVehicles = Array.from(uniqueMap.values());
  }

  applyFilters() {
    this.filteredVehicles = this.uniqueVehicles.filter(vehicle => {
      const categoryMatch = !this.selectedCategory || vehicle.category === this.selectedCategory;
      const brandMatch = !this.selectedBrand || vehicle.brand === this.selectedBrand;
      return categoryMatch && brandMatch;
    });
  }

  onCategoryChange() {
    this.applyFilters();
  }

  onBrandChange() {
    this.applyFilters();
  }

  clearFilters() {
    this.selectedCategory = '';
    this.selectedBrand = '';
    this.filteredVehicles = [...this.uniqueVehicles];
  }

  viewVehicleDetails(vehicleId: number) {
    this.router.navigate(['/vehicle', vehicleId]);
  }
}
