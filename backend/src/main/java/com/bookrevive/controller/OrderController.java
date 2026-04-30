package com.bookrevive.controller;

import com.bookrevive.model.Order;
import com.bookrevive.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{userId}/create")
    public ResponseEntity<?> createOrder(@PathVariable String userId, @RequestBody Map<String, String> details) {
        Map<String, Object> response = orderService.createOrderFromCart(userId, details);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable String userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String orderId, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Map<String, Object> response = orderService.updateOrderStatus(orderId, status);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }
}
