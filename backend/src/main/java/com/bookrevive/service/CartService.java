package com.bookrevive.service;

import com.bookrevive.model.Book;
import com.bookrevive.model.Cart;
import com.bookrevive.model.CartItem;
import com.bookrevive.repository.BookRepository;
import com.bookrevive.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    // ─── GET / CREATE ──────────────────────────────────────────────────────────

    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setUpdatedAt(LocalDateTime.now());
            return cartRepository.save(cart);
        });
    }

    // ─── ADD TO CART ───────────────────────────────────────────────────────────

    public Map<String, Object> addToCart(String userId, String bookId, int quantity) {
        Map<String, Object> response = new HashMap<>();

        Optional<Book> optBook = bookRepository.findById(bookId);
        if (optBook.isEmpty()) {
            response.put("success", false);
            response.put("message", "Book not found");
            return response;
        }

        Book book = optBook.get();
        if (Boolean.FALSE.equals(book.getAvailable())) {
            response.put("success", false);
            response.put("message", "Book is no longer available");
            return response;
        }

        Cart cart = getOrCreateCart(userId);

        // Determine which list to use
        List<CartItem> targetList = "DONATE".equalsIgnoreCase(book.getType())
                ? cart.getDonateItems()
                : cart.getBuyItems();

        // Check if already in cart - update quantity
        Optional<CartItem> existing = targetList.stream()
                .filter(ci -> ci.getBookId().equals(bookId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setBookId(bookId);
            item.setTitle(book.getTitle());
            item.setAuthor(book.getAuthor());
            item.setPrice(book.getPrice());
            item.setType(book.getType());
            item.setCategory(book.getCategory());
            item.setCondition(book.getCondition());
            item.setQuantity(quantity);
            item.setOwnerId(book.getUserId());
            item.setOwnerName(book.getOwnerName());
            targetList.add(item);
        }

        recalculate(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        response.put("success", true);
        response.put("message", "Book added to cart");
        response.put("cart", cart);
        return response;
    }

    // ─── REMOVE FROM CART ──────────────────────────────────────────────────────

    public Map<String, Object> removeFromCart(String userId, String bookId) {
        Map<String, Object> response = new HashMap<>();
        Cart cart = getOrCreateCart(userId);

        boolean removedFromBuy    = cart.getBuyItems().removeIf(ci -> ci.getBookId().equals(bookId));
        boolean removedFromDonate = cart.getDonateItems().removeIf(ci -> ci.getBookId().equals(bookId));

        if (!removedFromBuy && !removedFromDonate) {
            response.put("success", false);
            response.put("message", "Item not found in cart");
            return response;
        }

        recalculate(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        response.put("success", true);
        response.put("message", "Item removed from cart");
        response.put("cart", cart);
        return response;
    }

    // ─── UPDATE QUANTITY ───────────────────────────────────────────────────────

    public Map<String, Object> updateQuantity(String userId, String bookId, int newQuantity) {
        Map<String, Object> response = new HashMap<>();

        if (newQuantity <= 0) {
            return removeFromCart(userId, bookId);
        }

        Cart cart = getOrCreateCart(userId);

        boolean updated = false;
        for (CartItem item : cart.getBuyItems()) {
            if (item.getBookId().equals(bookId)) {
                item.setQuantity(newQuantity);
                updated = true;
                break;
            }
        }
        if (!updated) {
            for (CartItem item : cart.getDonateItems()) {
                if (item.getBookId().equals(bookId)) {
                    item.setQuantity(newQuantity);
                    updated = true;
                    break;
                }
            }
        }

        if (!updated) {
            response.put("success", false);
            response.put("message", "Item not found in cart");
            return response;
        }

        recalculate(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        response.put("success", true);
        response.put("message", "Quantity updated");
        response.put("cart", cart);
        return response;
    }

    // ─── CLEAR CART ────────────────────────────────────────────────────────────

    public Map<String, Object> clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getBuyItems().clear();
        cart.getDonateItems().clear();
        recalculate(cart);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Cart cleared");
        return response;
    }

    // ─── HELPERS ───────────────────────────────────────────────────────────────

    private void recalculate(Cart cart) {
        double total = cart.getBuyItems().stream()
                .mapToDouble(ci -> ci.getPrice() * ci.getQuantity())
                .sum();
        int count = cart.getBuyItems().stream().mapToInt(CartItem::getQuantity).sum()
                  + cart.getDonateItems().stream().mapToInt(CartItem::getQuantity).sum();

        cart.setBuyTotal(total);
        cart.setItemCount(count);
    }
}
