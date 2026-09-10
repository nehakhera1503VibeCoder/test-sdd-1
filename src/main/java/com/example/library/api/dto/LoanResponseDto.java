package com.example.library.api.dto;

import com.example.library.domain.Loan;
import com.example.library.domain.LoanStatus;
import java.time.LocalDate;

/** FR-4/FR-5/FR-6/FR-7 response shape. {@code status} is computed as-of "today". */
public class LoanResponseDto {

    private final String id;
    private final String bookId;
    private final String memberId;
    private final LocalDate borrowedDate;
    private final LocalDate dueDate;
    private final LocalDate returnedDate;
    private final LoanStatus status;

    public LoanResponseDto(Loan loan, LocalDate asOf) {
        this.id = loan.getId();
        this.bookId = loan.getBookId();
        this.memberId = loan.getMemberId();
        this.borrowedDate = loan.getBorrowedDate();
        this.dueDate = loan.getDueDate();
        this.returnedDate = loan.getReturnedDate();
        this.status = loan.statusAsOf(asOf);
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

    public LoanStatus getStatus() {
        return status;
    }
}
