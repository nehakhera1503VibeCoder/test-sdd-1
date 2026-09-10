package com.example.library.domain;

/**
 * Not stored on {@link Loan} directly — computed from {@code returnedDate}
 * and {@code dueDate} as of a given date via {@link Loan#statusAsOf}, so
 * "overdue" can never silently drift out of sync with the current date
 * (design decision, docs/02-techlead-design.md §4).
 */
public enum LoanStatus {
    ACTIVE,
    RETURNED,
    OVERDUE
}
