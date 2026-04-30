package com.bookrevive.controller;

import com.bookrevive.model.Cart;
import com.bookrevive.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<?> addToCart(@PathVariable String userId, @RequestBody Map<String, Object> body) {
        String bookId = (String) body.get("bookId");
        int quantity = (int) body.getOrDefault("quantity", 1);
        
        Map<String, Object> response = cartService.addToCart(userId, bookId, quantity);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{userId}/remove/{bookId}")
    public ResponseEntity<?> removeFromCart(@PathVariable String userId, @PathVariable String bookId) {
        Map<String, Object> response = cartService.removeFromCart(userId, bookId);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<?> updateQuantity(@PathVariable String userId, @RequestBody Map<String, Object> body) {
        String bookId = (String) body.get("bookId");
        int quantity = (int) body.get("quantity");
        
        Map<String, Object> response = cartService.updateQuantity(userId, bookId, quantity);
        if ((boolean) response.get("success")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<?> clearCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.clearCart(userId));
    }
}
