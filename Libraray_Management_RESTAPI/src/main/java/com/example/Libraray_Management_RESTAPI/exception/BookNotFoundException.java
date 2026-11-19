package com.example.Libraray_Management_RESTAPI.exception;

/**
 * (e) Thrown when no book exists with the given ID.
 * GlobalExceptionHandler turns it into a 404 Not Found response.
 */
public class BookNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BookNotFoundException(Integer id) {
        super("Book not found with ID: " + id);
    }
}
