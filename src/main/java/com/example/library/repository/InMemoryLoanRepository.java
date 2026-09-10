package com.example.library.repository;

import com.example.library.domain.Loan;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryLoanRepository implements LoanRepository {

    private final Map<String, Loan> loansById = new ConcurrentHashMap<>();

    @Override
    public Loan save(Loan loan) {
        loansById.put(loan.getId(), loan);
        return loan;
    }

    @Override
    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loansById.get(id));
    }

    @Override
    public List<Loan> findAll() {
        return List.copyOf(loansById.values());
    }

    @Override
    public List<Loan> findByMemberId(String memberId) {
        return loansById.values().stream()
                .filter(l -> l.getMemberId().equals(memberId))
                .toList();
    }

    @Override
    public List<Loan> findActiveByMemberId(String memberId) {
        return loansById.values().stream()
                .filter(l -> l.getMemberId().equals(memberId) && !l.isReturned())
                .toList();
    }

    @Override
    public List<Loan> findActiveByMemberIdAndBookId(String memberId, String bookId) {
        return loansById.values().stream()
                .filter(l -> l.getMemberId().equals(memberId)
                        && l.getBookId().equals(bookId)
                        && !l.isReturned())
                .toList();
    }
}
