import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/orders';

  constructor() { }

  getUserOrders(userId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${userId}`);
  }

  createOrder(userId: string, orderData: any): Observable<any> {
    // Expected orderData: { shippingAddress, ... } (if backend accepts json)
    // The OrderController maps it as @RequestBody Map<String, Object> request
    return this.http.post<any>(`${this.apiUrl}/${userId}`, orderData);
  }

  getOrderDetails(orderId: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/order/${orderId}`);
  }
}
