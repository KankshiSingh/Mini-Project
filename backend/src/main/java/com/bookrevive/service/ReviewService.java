package com.bookrevive.service;

import com.bookrevive.model.Book;
import com.bookrevive.model.Review;
import com.bookrevive.repository.BookRepository;
import com.bookrevive.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<Review> getReviewsForBook(String bookId) {
        return reviewRepository.findByBookId(bookId);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review addReview(Review review) {
        review.setCreatedAt(LocalDateTime.now());
        Review savedReview = reviewRepository.save(review);
        updateBookRating(review.getBookId());
        return savedReview;
    }

    public Review updateReview(String id, Review updatedReview) {
        return reviewRepository.findById(id).map(review -> {
            review.setRating(updatedReview.getRating());
            review.setReviewText(updatedReview.getReviewText());
            Review saved = reviewRepository.save(review);
            updateBookRating(review.getBookId());
            return saved;
        }).orElseThrow(() -> new RuntimeException("Review not found"));
    }

    public void deleteReview(String id) {
        reviewRepository.findById(id).ifPresent(review -> {
            reviewRepository.deleteById(id);
            updateBookRating(review.getBookId());
        });
    }

    private void updateBookRating(String bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        bookRepository.findById(bookId).ifPresent(book -> {
            if (reviews.isEmpty()) {
                book.setAverageRating(0.0);
                book.setReviewCount(0);
            } else {
                double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
                book.setAverageRating(avg);
                book.setReviewCount(reviews.size());
            }
            bookRepository.save(book);
        });
    }
}
