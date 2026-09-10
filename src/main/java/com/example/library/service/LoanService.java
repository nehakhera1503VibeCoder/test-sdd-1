package com.example.library.service;

import com.example.library.domain.Loan;
import java.util.List;

public interface LoanService {

    /**
     * FR-4. Throws NotFoundException if the book or member doesn't exist;
     * ConflictException if the book has no available copy, or the member
     * already has an active (unreturned) loan for that same book.
     */
    Loan borrowBook(String bookId, String memberId);

    /**
     * FR-5. Throws NotFoundException if no such loan; ConflictException if
     * the loan was already returned.
     */
    Loan returnBook(String loanId);

    /** FR-6. Throws NotFoundException if no such member. */
    List<Loan> listActiveLoansForMember(String memberId);

    /** FR-7. */
    List<Loan> listOverdueLoans();
}
