package com.example.library.service.impl;

import com.example.library.domain.Book;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.BookRepository;
import com.example.library.service.BookService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(String isbn, String title, String author, int totalCopies) {
        bookRepository.findByIsbn(isbn).ifPresent(existing -> {
            throw new ConflictException("A book with ISBN " + isbn + " already exists (id=" + existing.getId() + ")");
        });
        Book book = new Book(UUID.randomUUID().toString(), isbn, title, author, totalCopies);
        return bookRepository.save(book);
    }

    @Override
    public List<Book> listBooks(String query) {
        if (query == null || query.isBlank()) {
            return bookRepository.findAll();
        }
        return bookRepository.search(query);
    }

    @Override
    public Book getBook(String bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("No book with id " + bookId));
    }
}
