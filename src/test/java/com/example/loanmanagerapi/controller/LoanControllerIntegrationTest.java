package com.example.loanmanagerapi.controller;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.repository.LoanRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LoanControllerFullIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoanRepository loanRepository; // so we can verify the actual data

    @BeforeEach
    void setup() {
        // Clear the H2 database before each test
        loanRepository.deleteAll();
    }

    @Test
    void createLoan_shouldPersistAndReturnLoan() throws Exception {
        // Arrange: Build a Loan object to send as JSON
        Loan testLoan = new Loan();
        testLoan.setBookTitle("Integration Test Book");
        testLoan.setAuthor("Test Author");
        testLoan.setEmail("test@example.com");
        testLoan.setStatus("Active");

        String jsonRequest = objectMapper.writeValueAsString(testLoan);

        // Act: POST /api/loans/create
        mockMvc.perform(post("/api/loans/create")
                        .contentType("application/json")
                        .content(jsonRequest))
                .andExpect(status().isOk())    // or isCreated() if your controller does 201
                .andExpect(jsonPath("$.bookTitle").value("Integration Test Book"))
                .andExpect(jsonPath("$.author").value("Test Author"));

        // Assert: Check the DB to verify it was actually saved
        List<Loan> allLoans = loanRepository.findAll();
        assertEquals(1, allLoans.size());
        assertEquals("Integration Test Book", allLoans.get(0).getBookTitle());
    }

    @Test
    void getAllOverdueLoans_shouldReturnOverdueLoansFromDB() throws Exception {
        // Arrange: Insert a couple of Loans directly into the DB
        Loan overdueLoan = new Loan();
        overdueLoan.setBookTitle("Overdue Book");
        overdueLoan.setAuthor("Author A");
        overdueLoan.setOverdue(true);

        Loan onTimeLoan = new Loan();
        onTimeLoan.setBookTitle("On Time Book");
        onTimeLoan.setAuthor("Author B");
        onTimeLoan.setOverdue(false);

        loanRepository.save(overdueLoan);
        loanRepository.save(onTimeLoan);

        // Act: GET /api/loans/overdue
        mockMvc.perform(get("/api/loans/overdue")
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Overdue Book"))
                .andExpect(jsonPath("$[0].overdue").value(true));

        // Assert: We only expect the overdue one
        // We could verify count, or rely on the JSON checks above
    }
}
