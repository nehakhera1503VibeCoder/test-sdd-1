package com.example.library;

import java.time.Clock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

/**
 * Entry point. See AGENTS.md (or CLAUDE.md in Claude Code) for how this
 * codebase is organized and what process built it
 * (docs/03-spec-driven-development-playbook.md).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    /**
     * A single injectable {@link Clock} bean so due-date and overdue
     * calculations (DES-001 §4) can be deterministically tested instead of
     * calling {@code LocalDate.now()} directly throughout the service layer.
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
