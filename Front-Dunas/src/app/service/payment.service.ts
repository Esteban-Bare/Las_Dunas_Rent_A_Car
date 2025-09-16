import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private readonly baseUrl = 'http://localhost:8077/rental/payments';

  async getUserPendingPayments(userId: number): Promise<any[]> {
    const response = await fetch(`${this.baseUrl}/user/${userId}`, {
      method: 'GET',
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error('Failed to fetch user payments');
    }

    const allPayments = await response.json();
    return allPayments.filter((payment: any) => payment.paymentStatus === 'PENDING');
  }

  async processPayment(paymentId: number, paymentMethod: string): Promise<any> {
    const response = await fetch(`${this.baseUrl}/${paymentId}/process`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ paymentMethod }),
      credentials: 'include'
    });

    if (!response.ok) {
      throw new Error('Failed to process payment');
    }

    return response.json();
  }
}
