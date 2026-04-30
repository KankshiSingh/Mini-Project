package com.bookrevive.repository;

import com.bookrevive.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends MongoRepository<Book, String> {

    // Get books by owner
    List<Book> findByUserId(String userId);

    // Get books by type (SELL or DONATE)
    List<Book> findByType(String type);

    // Get books by category
    List<Book> findByCategory(String category);

    // Get available books only
    List<Book> findByAvailable(Boolean available);

    // Get available books by type
    List<Book> findByTypeAndAvailable(String type, Boolean available);

    // Search by title (case-insensitive)
    @Query("{ 'title': { $regex: ?0, $options: 'i' } }")
    List<Book> findByTitleContainingIgnoreCase(String title);

    // Search by author (case-insensitive)
    @Query("{ 'author': { $regex: ?0, $options: 'i' } }")
    List<Book> findByAuthorContainingIgnoreCase(String author);

    // Search by title OR author OR category
    @Query("{ $or: [ " +
           "{ 'title': { $regex: ?0, $options: 'i' } }, " +
           "{ 'author': { $regex: ?0, $options: 'i' } }, " +
           "{ 'category': { $regex: ?0, $options: 'i' } } " +
           "] }")
    List<Book> searchBooks(String query);

    // Get donation books
    List<Book> findByTypeAndAvailableOrderByListedAtDesc(String type, Boolean available);

    // Get books by category in user's search history (for recommendations)
    List<Book> findByCategoryIn(List<String> categories);
}
