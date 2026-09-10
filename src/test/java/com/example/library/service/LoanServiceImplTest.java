package com.example.library.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.config.LoanPolicyProperties;
import com.example.library.domain.Book;
import com.example.library.domain.Loan;
import com.example.library.domain.LoanStatus;
import com.example.library.domain.Member;
import com.example.library.exception.ConflictException;
import com.example.library.exception.NotFoundException;
import com.example.library.repository.InMemoryBookRepository;
import com.example.library.repository.InMemoryLoanRepository;
import com.example.library.repository.InMemoryMemberRepository;
import com.example.library.service.impl.BookServiceImpl;
import com.example.library.service.impl.LoanServiceImpl;
import com.example.library.service.impl.MemberServiceImpl;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * FR-4, FR-5, FR-6, FR-7. Uses a settable {@link Clock} (design decision,
 * docs/02-techlead-design.md §4) so due-date and overdue calculations are
 * asserted against known, hand-computed dates rather than "now".
 */
class LoanServiceImplTest {

    /** 2026-01-01 in UTC — dueDate with the default 14-day policy is 2026-01-15. */
    private static final LocalDate BORROW_DATE = LocalDate.of(2026, 1, 1);
    private static final LocalDate EXPECTED_DUE_DATE = LocalDate.of(2026, 1, 15);

    private SettableClock clock;
    private BookService bookService;
    private MemberService memberService;
    private LoanServiceImpl loanService;

    private String bookId;
    private String memberId;

    @BeforeEach
    void setUp() {
        clock = new SettableClock(BORROW_DATE.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC);
        bookService = new BookServiceImpl(new InMemoryBookRepository());
        memberService = new MemberServiceImpl(new InMemoryMemberRepository());
        LoanPolicyProperties loanPolicy = new LoanPolicyProperties();
        loanPolicy.setLoanPeriodDays(14);
        loanService = new LoanServiceImpl(new InMemoryLoanRepository(), bookService, memberService, clock, loanPolicy);

        bookId = bookService.addBook("111", "Domain-Driven Design", "Eric Evans", 1).getId();
        memberId = memberService.registerMember("Ada Lovelace", "ada@example.com").getId();
    }

    @Test
    void borrowBook_computesDueDateFromConfiguredLoanPeriod_andDecrementsAvailableCopies() {
        Loan loan = loanService.borrowBook(bookId, memberId);

        assertThat(loan.getBorrowedDate()).isEqualTo(BORROW_DATE);
        assertThat(loan.getDueDate()).isEqualTo(EXPECTED_DUE_DATE); // 2026-01-01 + 14 days, hand-checked
        assertThat(loan.isReturned()).isFalse();
        assertThat(bookService.getBook(bookId).getAvailableCopies()).isEqualTo(0);
    }

    @Test
    void borrowBook_noAvailableCopies_throwsConflict() {
        loanService.borrowBook(bookId, memberId); // takes the single copy

        String otherMemberId = memberService.registerMember("Bob", "bob@example.com").getId();

        assertThatThrownBy(() -> loanService.borrowBook(bookId, otherMemberId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("No available copies");
    }

    @Test
    void borrowBook_sameMemberSameBookAlreadyActive_throwsConflictEvenWithCopiesAvailable() {
        String multiCopyBookId = bookService.addBook("222", "Clean Code", "Robert Martin", 2).getId();
        loanService.borrowBook(multiCopyBookId, memberId);

        assertThatThrownBy(() -> loanService.borrowBook(multiCopyBookId, memberId))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already has an active loan");
    }

    @Test
    void borrowBook_unknownBookOrMember_throwsNotFound() {
        assertThatThrownBy(() -> loanService.borrowBook("no-such-book", memberId))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> loanService.borrowBook(bookId, "no-such-member"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void returnBook_marksReturnedAndRestoresAvailableCopy() {
        Loan loan = loanService.borrowBook(bookId, memberId);
        clock.set(BORROW_DATE.plusDays(3).atStartOfDay(ZoneOffset.UTC).toInstant());

        Loan returned = loanService.returnBook(loan.getId());

        assertThat(returned.isReturned()).isTrue();
        assertThat(returned.getReturnedDate()).isEqualTo(BORROW_DATE.plusDays(3));
        assertThat(bookService.getBook(bookId).getAvailableCopies()).isEqualTo(1);
    }

    @Test
    void returnBook_alreadyReturned_throwsConflict() {
        Loan loan = loanService.borrowBook(bookId, memberId);
        loanService.returnBook(loan.getId());

        assertThatThrownBy(() -> loanService.returnBook(loan.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already returned");
    }

    @Test
    void returnBook_unknownLoan_throwsNotFound() {
        assertThatThrownBy(() -> loanService.returnBook("no-such-loan"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void listActiveLoansForMember_excludesReturnedLoans() {
        String book2Id = bookService.addBook("222", "Clean Code", "Robert Martin", 1).getId();
        Loan loan1 = loanService.borrowBook(bookId, memberId);
        loanService.borrowBook(book2Id, memberId);
        loanService.returnBook(loan1.getId());

        List<Loan> active = loanService.listActiveLoansForMember(memberId);

        assertThat(active).hasSize(1);
        assertThat(active.get(0).getBookId()).isEqualTo(book2Id);
    }

    @Test
    void listActiveLoansForMember_unknownMember_throwsNotFound() {
        assertThatThrownBy(() -> loanService.listActiveLoansForMember("no-such-member"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void listOverdueLoans_flipsOverdueOnlyStrictlyAfterDueDate() {
        Loan loan = loanService.borrowBook(bookId, memberId); // dueDate = 2026-01-15

        // Exactly on the due date: still ACTIVE, not overdue (hand-checked per statusAsOf's contract).
        clock.set(EXPECTED_DUE_DATE.atStartOfDay(ZoneOffset.UTC).toInstant());
        assertThat(loan.statusAsOf(LocalDate.now(clock))).isEqualTo(LoanStatus.ACTIVE);
        assertThat(loanService.listOverdueLoans()).isEmpty();

        // One day after the due date: OVERDUE.
        clock.set(EXPECTED_DUE_DATE.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant());
        List<Loan> overdue = loanService.listOverdueLoans();
        assertThat(overdue).extracting(Loan::getId).containsExactly(loan.getId());
    }

    @Test
    void listOverdueLoans_excludesReturnedLoansEvenIfReturnedLate() {
        Loan loan = loanService.borrowBook(bookId, memberId); // dueDate = 2026-01-15
        clock.set(EXPECTED_DUE_DATE.plusDays(5).atStartOfDay(ZoneOffset.UTC).toInstant());

        loanService.returnBook(loan.getId());

        assertThat(loanService.listOverdueLoans()).isEmpty();
    }

    /** Minimal mutable {@link Clock} test double — advances "now" between borrow/return calls. */
    private static final class SettableClock extends Clock {
        private Instant instant;
        private final ZoneId zone;

        SettableClock(Instant instant, ZoneId zone) {
            this.instant = instant;
            this.zone = zone;
        }

        void set(Instant instant) {
            this.instant = instant;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new SettableClock(instant, zone);
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
