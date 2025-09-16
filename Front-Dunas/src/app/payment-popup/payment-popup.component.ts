import { Component, EventEmitter, Input, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface PaymentItem {
  id: number;
  amount: number;
  paymentType: string;
  description: string;
  status: string;
}

@Component({
  selector: 'app-payment-popup',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-popup.component.html',
  styleUrl: './payment-popup.component.css'
})
export class PaymentPopupComponent implements OnInit {
  @Input() isVisible: boolean = false;
  @Input() payments: PaymentItem[] = [];
  @Input() title: string = 'Process Payments';
  @Output() paymentCompleted = new EventEmitter<void>();
  @Output() paymentCancelled = new EventEmitter<void>();

  selectedPaymentMethod: string = 'CREDIT_CARD';
  isProcessing: boolean = false;

  ngOnInit() {}

  getTotalAmount(): number {
    return this.payments.reduce((total, payment) => total + payment.amount, 0);
  }

  closePopup() {
    this.paymentCancelled.emit();
  }

  onOverlayClick(event: Event) {
    this.closePopup();
  }

  async processPayments() {
    if (this.payments.length === 0) return;

    this.isProcessing = true;

    try {
      const paymentPromises = this.payments.map(payment =>
        this.processIndividualPayment(payment.id, this.selectedPaymentMethod)
      );

      await Promise.all(paymentPromises);

      console.log('All payments processed successfully');
      this.paymentCompleted.emit();
    } catch (error) {
      console.error('Error processing payments:', error);
      alert('Failed to process payments. Please try again.');
    } finally {
      this.isProcessing = false;
    }
  }

  private async processIndividualPayment(id: number, paymentMethod: string): Promise<any> {
    const response = await fetch(`http://localhost:8077/rental/payments/${id}/process`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ paymentMethod }),
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error(`Failed to process payment ${id}`);
    }

    return response.json();
  }
}
