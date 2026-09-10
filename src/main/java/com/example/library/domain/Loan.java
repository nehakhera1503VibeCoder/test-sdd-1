package com.example.library.domain;

import java.time.LocalDate;
import java.util.Objects;

/**
 * One borrow of one copy of one book by one member (FR-4/FR-5/FR-6/FR-7).
 */
public class Loan {

    private final String id;
    private final String bookId;
    private final String memberId;
    private final LocalDate borrowedDate;
    private final LocalDate dueDate;
    private LocalDate returnedDate;

    public Loan(String id, String bookId, String memberId, LocalDate borrowedDate, LocalDate dueDate) {
        this.id = Objects.requireNonNull(id, "id");
        this.bookId = Objects.requireNonNull(bookId, "bookId");
        this.memberId = Objects.requireNonNull(memberId, "memberId");
        this.borrowedDate = Objects.requireNonNull(borrowedDate, "borrowedDate");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMemberId() {
        return memberId;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public boolean isReturned() {
        return returnedDate != null;
    }

    public void markReturned(LocalDate returnedDate) {
        if (isReturned()) {
            throw new IllegalStateException("Loan " + id + " was already returned on " + this.returnedDate);
        }
        this.returnedDate = Objects.requireNonNull(returnedDate, "returnedDate");
    }

    /** FR-7: a loan is OVERDUE only while unreturned and past its due date, as of {@code asOf}. */
    public LoanStatus statusAsOf(LocalDate asOf) {
        if (isReturned()) {
            return LoanStatus.RETURNED;
        }
        return asOf.isAfter(dueDate) ? LoanStatus.OVERDUE : LoanStatus.ACTIVE;
    }
}
