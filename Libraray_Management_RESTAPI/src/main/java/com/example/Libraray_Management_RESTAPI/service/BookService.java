package com.example.Libraray_Management_RESTAPI.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import com.example.Libraray_Management_RESTAPI.exception.BookNotFoundException;
import com.example.Libraray_Management_RESTAPI.model.Book;

/**
 * Business logic for book records.
 * Books are kept in memory (a Map), so no database is needed.
 * Data resets every time the application restarts.
 */
@Service
public class BookService {

    private final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    // Some sample books so the library is not empty at startup
    public BookService() {
        addBook(new Book(null, "Clean Code", "Robert C. Martin", "Programming", 5));
        addBook(new Book(null, "Introduction to Algorithms", "Thomas H. Cormen", "Computer Science", 3));
        addBook(new Book(null, "Head First Java", "Kathy Sierra", "Programming", 7));
        addBook(new Book(null, "Database System Concepts", "Abraham Silberschatz", "Databases", 4));
        addBook(new Book(null, "Wings of Fire", "A. P. J. Abdul Kalam", "Biography", 0));
    }

    // (b) Add a new book. The server always assigns the ID.
    public Book addBook(Book book) {
        int id = nextId.getAndIncrement();
        book.setId(id);
        clean(book);
        books.put(id, book);
        return book;
    }

    // (c) Get all books, sorted by ID
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>(books.values());
        list.sort(Comparator.comparing(Book::getId));
        return list;
    }

    // (c) Get one book by ID, or throw BookNotFoundException
    public Book getBookById(Integer id) {
        Book book = books.get(id);
        if (book == null) {
            throw new BookNotFoundException(id);
        }
        return book;
    }

    // (d) Update title, author, category and available copies of a book
    public Book updateBook(Integer id, Book details) {
        Book book = getBookById(id);
        book.setTitle(details.getTitle());
        book.setAuthor(details.getAuthor());
        book.setCategory(details.getCategory());
        book.setAvailableCopies(details.getAvailableCopies());
        clean(book);
        return book;
    }

    // (e) Delete a book by ID, or throw BookNotFoundException
    public Book deleteBook(Integer id) {
        Book removed = books.remove(id);
        if (removed == null) {
            throw new BookNotFoundException(id);
        }
        return removed;
    }

    // Remove extra spaces around text fields
    private void clean(Book book) {
        book.setTitle(book.getTitle().trim());
        book.setAuthor(book.getAuthor().trim());
        book.setCategory(book.getCategory().trim());
    }
}
