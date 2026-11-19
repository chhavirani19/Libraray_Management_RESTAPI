package com.example.Libraray_Management_RESTAPI.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Libraray_Management_RESTAPI.model.Book;
import com.example.Libraray_Management_RESTAPI.service.BookService;

import jakarta.validation.Valid;

/**
 * REST endpoints for library books.
 * Base URL: http://localhost:8080/api/books
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // (b) Add a new book -> 201 Created
    @PostMapping
    public ResponseEntity<Book> addBook(@Valid @RequestBody Book book) {
        Book saved = bookService.addBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // (c) Get all books -> 200 OK
    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    // (c) Get one book by ID -> 200 OK, or 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Integer id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // (d) Update title, author, category and available copies -> 200 OK, or 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Integer id, @Valid @RequestBody Book book) {
        return ResponseEntity.ok(bookService.updateBook(id, book));
    }

    // (e) Delete a book by ID -> 200 OK with a message, or 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBook(@PathVariable Integer id) {
        Book deleted = bookService.deleteBook(id);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Book with ID " + id + " deleted successfully");
        response.put("deletedBook", deleted);
        return ResponseEntity.ok(response);
    }
}
