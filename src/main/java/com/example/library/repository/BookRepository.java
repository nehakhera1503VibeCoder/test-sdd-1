package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(String id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAll();

    /** FR-2: case-insensitive substring match against title or author. */
    List<Book> search(String query);
}
