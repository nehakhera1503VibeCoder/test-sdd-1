package com.example.library.api;

import com.example.library.api.dto.BookRequestDto;
import com.example.library.api.dto.BookResponseDto;
import com.example.library.domain.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** FR-1, FR-2 (docs/01-po-requirements.md). */
@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponseDto addBook(@Valid @RequestBody BookRequestDto request) {
        Book book = bookService.addBook(request.getIsbn(), request.getTitle(), request.getAuthor(), request.getTotalCopies());
        return new BookResponseDto(book);
    }

    @GetMapping
    public List<BookResponseDto> listBooks(@RequestParam(required = false) String query) {
        return bookService.listBooks(query).stream().map(BookResponseDto::new).toList();
    }

    @GetMapping("/{bookId}")
    public BookResponseDto getBook(@PathVariable String bookId) {
        return new BookResponseDto(bookService.getBook(bookId));
    }
}
