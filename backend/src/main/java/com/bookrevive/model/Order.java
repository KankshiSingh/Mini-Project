package com.bookrevive.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {

    @Id
    private String id;
    private String orderId;      // Human-readable order ID (e.g., ORD-20240406-1234)

    private String userId;
    private String userName;
    private String userEmail;

    private List<OrderItem> items = new ArrayList<>();

    private Double totalAmount;
    private String status;       // PENDING, CONFIRMED, DELIVERED, CANCELLED

    private String paymentMethod;
    private String deliveryAddress;

    private LocalDateTime orderedAt;
    private LocalDateTime updatedAt;
}
