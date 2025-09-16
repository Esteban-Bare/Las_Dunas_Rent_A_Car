import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

interface PaymentDto {
  id: number;
  userId: number;
  amount: number;
  paymentDate: string;
  paymentType: string;
  paymentStatus: string;
  paymentMethod: string;
  transactionId: string;
  description: string;
  reservation?: any;
  rental?: any;
}

@Component({
  selector: 'app-backoffice-payments',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './backoffice-payments.component.html',
  styleUrl: './backoffice-payments.component.css'
})
export class BackofficePaymentsComponent implements OnInit {
  payments: PaymentDto[] = [];
  filteredPayments: PaymentDto[] = [];
  isLoading = false;
  error = '';

  // Filter options
  showOnlyPending = false;
  selectedPaymentType = '';
  selectedStatus = '';

  // Stats
  totalPayments = 0;
  pendingCount = 0;
  completedCount = 0;
  totalRevenue = 0;

  // Payment types and statuses
  paymentTypes = ['RESERVATION', 'RENTAL', 'DEPOSIT', 'DEPOSIT_RETURN', 'INSURANCE', 'FINE', 'DAMAGE_CHARGE', 'REFUND'];
  paymentStatuses = ['PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'];

  // Processing states
  processingPayments: Set<number> = new Set();

  constructor(private router: Router) {}

  ngOnInit() {
    this.loadPayments();
  }

  async loadPayments() {
    this.isLoading = true;
    this.error = '';

    try {
      const response = await fetch('http://localhost:8077/rental/payments/all', {
        method: 'GET',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to load payments');
      }

      this.payments = await response.json();
      this.applyFilters();
      this.calculateStats();

    } catch (error) {
      console.error('Error loading payments:', error);
      this.error = 'Failed to load payments. Please try again.';
    } finally {
      this.isLoading = false;
    }
  }

  applyFilters() {
    this.filteredPayments = this.payments.filter(payment => {
      if (this.showOnlyPending && payment.paymentStatus !== 'PENDING') {
        return false;
      }

      if (this.selectedPaymentType && payment.paymentType !== this.selectedPaymentType) {
        return false;
      }

      if (this.selectedStatus && payment.paymentStatus !== this.selectedStatus) {
        return false;
      }

      return true;
    });
  }

  calculateStats() {
    this.totalPayments = this.payments.length;
    this.pendingCount = this.payments.filter(p => p.paymentStatus === 'PENDING').length;
    this.completedCount = this.payments.filter(p => p.paymentStatus === 'COMPLETED').length;
    const response = fetch("http://localhost:8077/rental/payments/total-revenue", {
      method: 'GET',
      credentials: 'include'
    }).then(res => res.json()).then(data => {
      this.totalRevenue = data;
    }).catch(
      error => {
        console.error('Error fetching total revenue:', error);
        this.totalRevenue = 0;
      }
    )
  }

  onFilterChange() {
    this.applyFilters();
  }

  async processPayment(paymentId: number, paymentMethod: string = 'CASH') {
    if (this.processingPayments.has(paymentId)) return;

    this.processingPayments.add(paymentId);

    try {
      const response = await fetch(`http://localhost:8077/rental/payments/${paymentId}/process`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ paymentMethod })
      });

      if (!response.ok) {
        throw new Error('Failed to process payment');
      }

      await this.loadPayments();
      alert('Payment processed successfully!');

    } catch (error) {
      console.error('Error processing payment:', error);
      alert('Failed to process payment. Please try again.');
    } finally {
      this.processingPayments.delete(paymentId);
    }
  }

  async processDepositReturn(paymentId: number) {
    if (this.processingPayments.has(paymentId)) return;

    this.processingPayments.add(paymentId);

    try {
      const payment = this.payments.find(p => p.id === paymentId);
      if (!payment) throw new Error('Payment not found');

      const response = await fetch(`http://localhost:8077/rental/payments/${paymentId}/refund?amount=${payment.amount}`, {
        method: 'POST',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to process deposit return');
      }

      await this.loadPayments();
      alert('Deposit return processed successfully!');

    } catch (error) {
      console.error('Error processing deposit return:', error);
      alert('Failed to process deposit return. Please try again.');
    } finally {
      this.processingPayments.delete(paymentId);
    }
  }

  async processRefund(paymentId: number) {
    const payment = this.payments.find(p => p.id === paymentId);
    if (!payment) return;

    const refundAmount = prompt(`Enter refund amount (Max: €${payment.amount}):`);
    if (!refundAmount || isNaN(Number(refundAmount))) return;

    const amount = Number(refundAmount);
    if (amount <= 0 || amount > payment.amount) {
      alert('Invalid refund amount');
      return;
    }

    if (this.processingPayments.has(paymentId)) return;
    this.processingPayments.add(paymentId);

    try {
      const response = await fetch(`http://localhost:8077/rental/payments/${paymentId}/refund?amount=${amount}`, {
        method: 'POST',
        credentials: 'include'
      });

      if (!response.ok) {
        throw new Error('Failed to process refund');
      }

      await this.loadPayments();
      alert('Refund processed successfully!');

    } catch (error) {
      console.error('Error processing refund:', error);
      alert('Failed to process refund. Please try again.');
    } finally {
      this.processingPayments.delete(paymentId);
    }
  }

  getPaymentTypeClass(paymentType: string): string {
    switch (paymentType) {
      case 'RESERVATION': return 'type-reservation';
      case 'RENTAL': return 'type-rental';
      case 'DEPOSIT': return 'type-deposit';
      case 'DEPOSIT_RETURN': return 'type-deposit-return';
      case 'INSURANCE': return 'type-insurance';
      case 'FINE': return 'type-fine';
      case 'DAMAGE_CHARGE': return 'type-damage';
      case 'REFUND': return 'type-refund';
      default: return 'type-default';
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'status-pending';
      case 'COMPLETED': return 'status-completed';
      case 'FAILED': return 'status-failed';
      case 'REFUNDED': return 'status-refunded';
      default: return 'status-default';
    }
  }

  canProcessPayment(payment: PaymentDto): boolean {
    return payment.paymentStatus === 'PENDING' &&
      ['RENTAL', 'FINE', 'DAMAGE_CHARGE'].includes(payment.paymentType);
  }

  canProcessDepositReturn(payment: PaymentDto): boolean {
    return payment.paymentType === 'DEPOSIT' &&
      payment.paymentStatus === 'COMPLETED';
  }

  canProcessRefund(payment: PaymentDto): boolean {
    return payment.paymentStatus === 'COMPLETED' &&
      payment.paymentType !== 'REFUND' &&
      payment.paymentType !== 'DEPOSIT_RETURN';
  }

  getRelatedInfo(payment: PaymentDto): string {
    if (payment.reservation) {
      return `Reservation #${payment.reservation.id}`;
    }
    if (payment.rental) {
      return `Rental #${payment.rental.id}`;
    }
    return 'N/A';
  }

  goBackToBackoffice() {
    this.router.navigate(['/backoffice']);
  }
}
