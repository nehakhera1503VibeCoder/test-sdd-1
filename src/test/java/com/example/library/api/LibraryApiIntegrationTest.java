package com.example.library.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Full-stack (real Spring context, real REST endpoints) walk through every
 * FR in docs/01-po-requirements.md, in the same sequence a real client
 * would call them: add book -> register member -> borrow -> list active ->
 * return -> overdue (empty, since nothing is overdue "now").
 */
@SpringBootTest
@AutoConfigureMockMvc
class LibraryApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullLendingLifecycle_endToEnd() throws Exception {
        // FR-1: add a book with 1 copy.
        String bookJson = objectMapper.writeValueAsString(
                Map.of("isbn", "978-1", "title", "Refactoring", "author", "Martin Fowler", "totalCopies", 1));
        String bookResponse = mockMvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content(bookJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.availableCopies").value(1))
                .andReturn().getResponse().getContentAsString();
        String bookId = JsonPath.read(bookResponse, "$.id");

        // FR-2: it shows up in the catalog and in a matching search.
        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mockMvc.perform(get("/api/v1/books").param("query", "fowler"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Refactoring"));

        // FR-3: register a member.
        String memberJson = objectMapper.writeValueAsString(Map.of("name", "Grace Hopper", "email", "grace@example.com"));
        String memberResponse = mockMvc.perform(post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(memberJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String memberId = JsonPath.read(memberResponse, "$.id");

        // FR-4: borrow the book -> ACTIVE, dueDate present, book now has 0 available copies.
        String borrowJson = objectMapper.writeValueAsString(Map.of("bookId", bookId, "memberId", memberId));
        String loanResponse = mockMvc.perform(post("/api/v1/loans").contentType(MediaType.APPLICATION_JSON).content(borrowJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.dueDate").exists())
                .andReturn().getResponse().getContentAsString();
        String loanId = JsonPath.read(loanResponse, "$.id");

        mockMvc.perform(get("/api/v1/books/" + bookId)).andExpect(jsonPath("$.availableCopies").value(0));

        // FR-4 conflict path: no more copies for a second member.
        String memberJson2 = objectMapper.writeValueAsString(Map.of("name", "Alan Turing", "email", "alan@example.com"));
        String member2Response = mockMvc.perform(post("/api/v1/members").contentType(MediaType.APPLICATION_JSON).content(memberJson2))
                .andReturn().getResponse().getContentAsString();
        String member2Id = JsonPath.read(member2Response, "$.id");
        String borrowJson2 = objectMapper.writeValueAsString(Map.of("bookId", bookId, "memberId", member2Id));
        mockMvc.perform(post("/api/v1/loans").contentType(MediaType.APPLICATION_JSON).content(borrowJson2))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());

        // FR-6: the first member's active loans include this one.
        mockMvc.perform(get("/api/v1/members/" + memberId + "/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(loanId));

        // FR-7: nothing is overdue yet (due date is in the future relative to "now").
        mockMvc.perform(get("/api/v1/loans/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // FR-5: return the book -> RETURNED, book has its copy back, no longer in the active list.
        mockMvc.perform(post("/api/v1/loans/" + loanId + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"));
        mockMvc.perform(get("/api/v1/books/" + bookId)).andExpect(jsonPath("$.availableCopies").value(1));
        mockMvc.perform(get("/api/v1/members/" + memberId + "/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // FR-5 conflict path: returning the same loan twice is rejected.
        mockMvc.perform(post("/api/v1/loans/" + loanId + "/return"))
                .andExpect(status().isConflict());
    }

    @Test
    void addBook_blankTitle_returns400() throws Exception {
        String badJson = objectMapper.writeValueAsString(Map.of("isbn", "1", "title", "", "author", "X", "totalCopies", 1));
        mockMvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON).content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void getBook_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/v1/books/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
