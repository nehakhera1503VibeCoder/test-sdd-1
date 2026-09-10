package com.example.library.domain;

import java.util.Objects;

/**
 * A catalog entry. {@code availableCopies} is mutated by {@code LoanService}
 * as copies are borrowed/returned (FR-4, FR-5 in docs/01-po-requirements.md).
 */
public class Book {

    private final String id;
    private final String isbn;
    private final String title;
    private final String author;
    private final int totalCopies;
    private int availableCopies;

    public Book(String id, String isbn, String title, String author, int totalCopies) {
        this.id = Objects.requireNonNull(id, "id");
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
        this.author = Objects.requireNonNull(author, "author");
        if (totalCopies < 1) {
            throw new IllegalArgumentException("totalCopies must be >= 1");
        }
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean hasAvailableCopy() {
        return availableCopies > 0;
    }

    public void borrowOneCopy() {
        if (!hasAvailableCopy()) {
            throw new IllegalStateException("No available copies for book " + id);
        }
        availableCopies--;
    }

    public void returnOneCopy() {
        if (availableCopies >= totalCopies) {
            throw new IllegalStateException("Cannot return more copies than totalCopies for book " + id);
        }
        availableCopies++;
    }
}
