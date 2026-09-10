package com.example.library.api;

import com.example.library.api.dto.BorrowRequestDto;
import com.example.library.api.dto.LoanResponseDto;
import com.example.library.domain.Loan;
import com.example.library.service.LoanService;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** FR-4, FR-5, FR-6, FR-7 (docs/01-po-requirements.md). */
@RestController
@RequestMapping("/api/v1")
public class LoanController {

    private final LoanService loanService;
    private final Clock clock;

    public LoanController(LoanService loanService, Clock clock) {
        this.loanService = loanService;
        this.clock = clock;
    }

    @PostMapping("/loans")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponseDto borrowBook(@Valid @RequestBody BorrowRequestDto request) {
        Loan loan = loanService.borrowBook(request.getBookId(), request.getMemberId());
        return new LoanResponseDto(loan, LocalDate.now(clock));
    }

    @PostMapping("/loans/{loanId}/return")
    public LoanResponseDto returnBook(@PathVariable String loanId) {
        Loan loan = loanService.returnBook(loanId);
        return new LoanResponseDto(loan, LocalDate.now(clock));
    }

    @GetMapping("/loans/overdue")
    public List<LoanResponseDto> listOverdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return loanService.listOverdueLoans().stream().map(loan -> new LoanResponseDto(loan, today)).toList();
    }

    @GetMapping("/members/{memberId}/loans")
    public List<LoanResponseDto> listActiveLoansForMember(@PathVariable String memberId) {
        LocalDate today = LocalDate.now(clock);
        return loanService.listActiveLoansForMember(memberId).stream()
                .map(loan -> new LoanResponseDto(loan, today))
                .toList();
    }
}
