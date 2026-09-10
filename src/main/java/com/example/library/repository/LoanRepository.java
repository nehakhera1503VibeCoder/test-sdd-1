package com.example.library.repository;

import com.example.library.domain.Loan;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(String id);

    List<Loan> findAll();

    List<Loan> findByMemberId(String memberId);

    /** Active (unreturned) loans for a member — used by FR-4's duplicate-borrow check and FR-6. */
    List<Loan> findActiveByMemberId(String memberId);

    /** Active (unreturned) loans for a book — used by FR-4's duplicate-borrow check. */
    List<Loan> findActiveByMemberIdAndBookId(String memberId, String bookId);
}
