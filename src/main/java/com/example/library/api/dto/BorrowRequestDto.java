package com.example.library.api.dto;

import jakarta.validation.constraints.NotBlank;

/** FR-4 request body for {@code POST /api/v1/loans}. */
public class BorrowRequestDto {

    @NotBlank
    private String bookId;

    @NotBlank
    private String memberId;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
