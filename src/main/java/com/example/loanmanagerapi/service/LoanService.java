package com.example.loanmanagerapi.service;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    // Method to create a new loan
    public Loan createLoan(Loan loan) {
        loan.setCheckoutDate(LocalDate.now());
        loan.setExpiredDate(LocalDate.now().plusWeeks(2)); // Example 2-week loan period
        loan.setOverdue(false); // Set overdue to false initially
        loan.setExtensionCount(0); // Set initial extension count to 0
        loan.setStatus("Active"); // Set initial status to "Active" or a default status
        return loanRepository.save(loan);
    }

    // Method to extend a loan
    public Loan extendLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow();
        if (loan.isExtensionAllowed()) {
            loan.setExpiredDate(loan.getExpiredDate().plusWeeks(1)); // Example 1-week extension
            loan.setExtensionCount(loan.getExtensionCount() + 1);
            return loanRepository.save(loan);
        }
        return loan;
    }

    // Additional methods (e.g., check overdue status) can be added here
}
