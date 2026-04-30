package com.bookrevive.service;

import com.bookrevive.model.Cart;
import com.bookrevive.model.CartItem;
import com.bookrevive.model.Order;
import com.bookrevive.model.OrderItem;
import com.bookrevive.repository.CartRepository;
import com.bookrevive.repository.OrderRepository;
import com.bookrevive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    // ─── CREATE ORDER FROM CART ────────────────────────────────────────────────

    public Map<String, Object> createOrderFromCart(String userId, Map<String, String> details) {
        Map<String, Object> response = new HashMap<>();

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null || (cart.getBuyItems().isEmpty() && cart.getDonateItems().isEmpty())) {
            response.put("success", false);
            response.put("message", "Cart is empty");
            return response;
        }

        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setUserId(userId);

        // Attach user info
        userRepository.findById(userId).ifPresent(user -> {
            order.setUserName(user.getName());
            order.setUserEmail(user.getEmail());
        });

        // Build order items from buy list
        List<OrderItem> items = new ArrayList<>();
        for (CartItem ci : cart.getBuyItems()) {
            OrderItem oi = new OrderItem();
            oi.setBookId(ci.getBookId());
            oi.setTitle(ci.getTitle());
            oi.setAuthor(ci.getAuthor());
            oi.setPrice(ci.getPrice());
            oi.setType(ci.getType());
            oi.setCategory(ci.getCategory());
            oi.setCondition(ci.getCondition());
            oi.setQuantity(ci.getQuantity());
            oi.setSubtotal(ci.getPrice() * ci.getQuantity());
            items.add(oi);

            // Mark book as unavailable and track purchase
            bookService.markUnavailable(ci.getBookId());
            userService.trackPurchase(userId, ci.getBookId());
        }

        // Include donated book requests (price=0) in order
        for (CartItem ci : cart.getDonateItems()) {
            OrderItem oi = new OrderItem();
            oi.setBookId(ci.getBookId());
            oi.setTitle(ci.getTitle());
            oi.setAuthor(ci.getAuthor());
            oi.setPrice(0.0);
            oi.setType("DONATE");
            oi.setCategory(ci.getCategory());
            oi.setCondition(ci.getCondition());
            oi.setQuantity(ci.getQuantity());
            oi.setSubtotal(0.0);
            items.add(oi);

            bookService.markUnavailable(ci.getBookId());
        }

        order.setItems(items);
        order.setTotalAmount(cart.getBuyTotal());
        order.setStatus("CONFIRMED");
        order.setPaymentMethod(details.getOrDefault("paymentMethod", "COD"));
        order.setDeliveryAddress(details.getOrDefault("deliveryAddress", ""));
        order.setOrderedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Clear cart after order
        cart.getBuyItems().clear();
        cart.getDonateItems().clear();
        cart.setBuyTotal(0.0);
        cart.setItemCount(0);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        response.put("success", true);
        response.put("message", "Order placed successfully");
        response.put("order", saved);
        return response;
    }

    // ─── GET ORDERS ────────────────────────────────────────────────────────────

    public List<Order> getOrdersByUser(String userId) {
        return orderRepository.findByUserIdOrderByOrderedAtDesc(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderedAtDesc();
    }

    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public Map<String, Object> updateOrderStatus(String orderId, String status) {
        Map<String, Object> response = new HashMap<>();
        Optional<Order> optOrder = orderRepository.findByOrderId(orderId);
        if (optOrder.isEmpty()) {
            response.put("success", false);
            response.put("message", "Order not found");
            return response;
        }
        Order order = optOrder.get();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        response.put("success", true);
        response.put("message", "Order status updated to " + status);
        response.put("order", order);
        return response;
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private String generateOrderId() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.valueOf((int)(Math.random() * 9000) + 1000);
        return "ORD-" + datePart + "-" + randomPart;
    }
}
