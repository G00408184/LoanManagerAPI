package com.example.loanmanagerapi.controller;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // Endpoint to create a new loan
    @PostMapping
    public Loan createLoan(@RequestBody Loan loan) {
        return loanService.createLoan(loan);
    }

    // Endpoint to extend a loan
    @PutMapping("/{loanId}/extend")
    public Loan extendLoan(@PathVariable Long loanId) {
        return loanService.extendLoan(loanId);
    }

    // Additional endpoints (e.g., get loan details) can be added here
}
