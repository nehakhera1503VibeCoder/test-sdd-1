package com.example.library.service;

import com.example.library.domain.Book;
import java.util.List;

public interface BookService {

    /** FR-1. Rejects a duplicate ISBN with ConflictException. */
    Book addBook(String isbn, String title, String author, int totalCopies);

    /** FR-2: no query returns the full catalog; a query filters by title/author substring. */
    List<Book> listBooks(String query);

    /** Throws NotFoundException if no such book. */
    Book getBook(String bookId);
}
