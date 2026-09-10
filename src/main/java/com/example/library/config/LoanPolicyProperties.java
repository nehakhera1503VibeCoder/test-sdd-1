package com.example.library.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code library.loan-policy.*} from application.yml (NFR-3): the
 * loan period is configuration, not a hardcoded constant in the service.
 */
@ConfigurationProperties(prefix = "library.loan-policy")
public class LoanPolicyProperties {

    /** Number of days between borrow date and due date. */
    private int loanPeriodDays = 14;

    public int getLoanPeriodDays() {
        return loanPeriodDays;
    }

    public void setLoanPeriodDays(int loanPeriodDays) {
        this.loanPeriodDays = loanPeriodDays;
    }
}
