package com.example.library.service.impl;

import com.example.library.config.LoanPolicyProperties;
import com.example.library.domain.Book;
import com.example.library.domain.Loan;
import com.example.library.domain.LoanStatus;
import com.example.library.domain.Member;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.LoanRepository;
import com.example.library.service.BookService;
import com.example.library.service.LoanService;
import com.example.library.service.MemberService;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final MemberService memberService;
    private final Clock clock;
    private final LoanPolicyProperties loanPolicy;

    public LoanServiceImpl(
            LoanRepository loanRepository,
            BookService bookService,
            MemberService memberService,
            Clock clock,
            LoanPolicyProperties loanPolicy) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.memberService = memberService;
        this.clock = clock;
        this.loanPolicy = loanPolicy;
    }

    @Override
    public Loan borrowBook(String bookId, String memberId) {
        Book book = bookService.getBook(bookId);
        Member member = memberService.getMember(memberId);

        if (!book.hasAvailableCopy()) {
            throw new ConflictException("No available copies of book " + book.getId());
        }
        if (!loanRepository.findActiveByMemberIdAndBookId(member.getId(), book.getId()).isEmpty()) {
            throw new ConflictException(
                    "Member " + member.getId() + " already has an active loan for book " + book.getId());
        }

        LocalDate borrowedDate = LocalDate.now(clock);
        LocalDate dueDate = borrowedDate.plusDays(loanPolicy.getLoanPeriodDays());
        Loan loan = new Loan(UUID.randomUUID().toString(), book.getId(), member.getId(), borrowedDate, dueDate);

        book.borrowOneCopy();
        return loanRepository.save(loan);
    }

    @Override
    public Loan returnBook(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new NotFoundException("No loan with id " + loanId));
        if (loan.isReturned()) {
            throw new ConflictException("Loan " + loanId + " was already returned");
        }
        loan.markReturned(LocalDate.now(clock));
        bookService.getBook(loan.getBookId()).returnOneCopy();
        return loanRepository.save(loan);
    }

    @Override
    public List<Loan> listActiveLoansForMember(String memberId) {
        memberService.getMember(memberId); // throws NotFoundException if unknown member
        return loanRepository.findActiveByMemberId(memberId);
    }

    @Override
    public List<Loan> listOverdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return loanRepository.findAll().stream()
                .filter(loan -> loan.statusAsOf(today) == LoanStatus.OVERDUE)
                .toList();
    }
}
