package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.domain.Book;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.InMemoryBookRepository;
import com.example.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** FR-1, FR-2. */
class BookServiceImplTest {

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(new InMemoryBookRepository());
    }

    @Test
    void addBook_createsBookWithAllCopiesAvailable() {
        Book book = bookService.addBook("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 3);

        assertThat(book.getId()).isNotBlank();
        assertThat(book.getTotalCopies()).isEqualTo(3);
        assertThat(book.getAvailableCopies()).isEqualTo(3);
    }

    @Test
    void addBook_rejectsDuplicateIsbn() {
        bookService.addBook("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 3);

        assertThatThrownBy(() -> bookService.addBook("978-0-13-468599-1", "Effective Java (dup)", "Someone", 1))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("978-0-13-468599-1");
    }

    @Test
    void listBooks_withNoQuery_returnsFullCatalog() {
        bookService.addBook("111", "Domain-Driven Design", "Eric Evans", 1);
        bookService.addBook("222", "Clean Code", "Robert Martin", 2);

        assertThat(bookService.listBooks(null)).hasSize(2);
        assertThat(bookService.listBooks("")).hasSize(2);
    }

    @Test
    void listBooks_withQuery_matchesTitleOrAuthorCaseInsensitively() {
        bookService.addBook("111", "Domain-Driven Design", "Eric Evans", 1);
        bookService.addBook("222", "Clean Code", "Robert Martin", 2);

        assertThat(bookService.listBooks("clean")).extracting(Book::getTitle).containsExactly("Clean Code");
        assertThat(bookService.listBooks("EVANS")).extracting(Book::getTitle).containsExactly("Domain-Driven Design");
        assertThat(bookService.listBooks("nonexistent")).isEmpty();
    }

    @Test
    void getBook_unknownId_throwsNotFound() {
        assertThatThrownBy(() -> bookService.getBook("does-not-exist"))
                .isInstanceOf(NotFoundException.class);
    }
}
