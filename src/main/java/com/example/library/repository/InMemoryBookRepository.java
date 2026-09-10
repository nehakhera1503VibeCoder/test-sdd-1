package com.example.library.repository;

import com.example.library.domain.Book;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * NFR-1 (docs/01-po-requirements.md): in-memory storage only for this demo,
 * no external database. Swapping in a JPA-backed implementation later is a
 * new Dev story against this same interface, not a rewrite of the service
 * layer — see docs/03-spec-driven-development-playbook.md §6.
 */
@Repository
public class InMemoryBookRepository implements BookRepository {

    private final Map<String, Book> booksById = new ConcurrentHashMap<>();

    @Override
    public Book save(Book book) {
        booksById.put(book.getId(), book);
        return book;
    }

    @Override
    public Optional<Book> findById(String id) {
        return Optional.ofNullable(booksById.get(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return booksById.values().stream()
                .filter(b -> b.getIsbn().equalsIgnoreCase(isbn))
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return List.copyOf(booksById.values());
    }

    @Override
    public List<Book> search(String query) {
        String needle = query.toLowerCase();
        return booksById.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(needle)
                        || b.getAuthor().toLowerCase().contains(needle))
                .toList();
    }
}
