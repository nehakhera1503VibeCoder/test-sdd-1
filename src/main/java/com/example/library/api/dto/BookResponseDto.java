package com.example.library.api.dto;

import com.example.library.domain.Book;

/** API-owned response shape (FR-1, FR-2) — decoupled from the domain {@link Book}. */
public class BookResponseDto {

    private final String id;
    private final String isbn;
    private final String title;
    private final String author;
    private final int totalCopies;
    private final int availableCopies;

    public BookResponseDto(Book book) {
        this.id = book.getId();
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.totalCopies = book.getTotalCopies();
        this.availableCopies = book.getAvailableCopies();
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
}
