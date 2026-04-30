package com.bookrevive.service;

import com.bookrevive.model.Book;
import com.bookrevive.repository.BookRepository;
import com.bookrevive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // ─── CRUD ──────────────────────────────────────────────────────────────────

    public Book addBook(Book book) {
        book.setAvailable(true);
        book.setListedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());

        // Attach owner info if userId provided
        if (book.getUserId() != null) {
            userRepository.findById(book.getUserId()).ifPresent(user -> {
                book.setOwnerName(user.getName());
                book.setOwnerEmail(user.getEmail());
            });
        }

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByAvailable(true);
    }

    public Optional<Book> getBookById(String id) {
        return bookRepository.findById(id);
    }

    public List<Book> getBooksByUser(String userId) {
        return bookRepository.findByUserId(userId);
    }

    public Book updateBook(String id, Book updates) {
        return bookRepository.findById(id).map(book -> {
            if (updates.getTitle() != null)       book.setTitle(updates.getTitle());
            if (updates.getAuthor() != null)      book.setAuthor(updates.getAuthor());
            if (updates.getPrice() != null)       book.setPrice(updates.getPrice());
            if (updates.getCategory() != null)    book.setCategory(updates.getCategory());
            if (updates.getCondition() != null)   book.setCondition(updates.getCondition());
            if (updates.getDescription() != null) book.setDescription(updates.getDescription());
            if (updates.getType() != null)        book.setType(updates.getType());
            if (updates.getAvailable() != null)   book.setAvailable(updates.getAvailable());
            book.setUpdatedAt(LocalDateTime.now());
            return bookRepository.save(book);
        }).orElseThrow(() -> new RuntimeException("Book not found: " + id));
    }

    public Map<String, Object> deleteBook(String id) {
        Map<String, Object> response = new HashMap<>();
        if (!bookRepository.existsById(id)) {
            response.put("success", false);
            response.put("message", "Book not found");
            return response;
        }
        bookRepository.deleteById(id);
        response.put("success", true);
        response.put("message", "Book deleted successfully");
        return response;
    }

    // ─── TYPE-BASED ────────────────────────────────────────────────────────────

    public List<Book> getSellBooks() {
        return bookRepository.findByTypeAndAvailable("SELL", true);
    }

    public List<Book> getDonationBooks() {
        return bookRepository.findByTypeAndAvailableOrderByListedAtDesc("DONATE", true);
    }

    // ─── SEARCH ────────────────────────────────────────────────────────────────

    public List<Book> searchBooks(String query) {
        if (query == null || query.isBlank()) {
            return bookRepository.findByAvailable(true);
        }
        return bookRepository.searchBooks(query.trim());
    }

    public List<Book> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category);
    }

    // ─── DONATION STATS ────────────────────────────────────────────────────────

    public Map<String, Object> getDonationStats() {
        List<Book> donations = bookRepository.findByType("DONATE");
        long available = donations.stream().filter(b -> Boolean.TRUE.equals(b.getAvailable())).count();
        long claimed   = donations.stream().filter(b -> Boolean.FALSE.equals(b.getAvailable())).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDonations", donations.size());
        stats.put("availableDonations", available);
        stats.put("claimedDonations", claimed);
        return stats;
    }

    // ─── MARK SOLD/CLAIMED ─────────────────────────────────────────────────────

    public void markUnavailable(String bookId) {
        bookRepository.findById(bookId).ifPresent(book -> {
            book.setAvailable(false);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.save(book);
        });
    }
}
