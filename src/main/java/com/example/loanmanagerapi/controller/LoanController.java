package com.example.loanmanagerapi.controller;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping
    public Loan createLoan(@RequestBody Loan loan) {
        return loanService.createLoan(loan);
    }

    @PostMapping("/admin")
    public Loan createLoanWithAdminCheck(
            @RequestBody Loan loan,
            @RequestParam Long userId,
            @RequestParam Long bookId
    ) {
        return loanService.createLoanWithAdminCheck(loan, userId, bookId);
    }

    @PutMapping("/{loanId}/extend")
    public Loan extendLoan(@PathVariable Long loanId) {
        return loanService.extendLoan(loanId);
    }

    @GetMapping("/{loanId}/days-left")
    public long getDaysLeft(@PathVariable Long loanId) {
        return loanService.getDaysLeft(loanId);
    }

    @GetMapping("/find")
    public Loan getLoanByTitleAndAuthor(
            @RequestParam("title") String bookTitle,
            @RequestParam("author") String author
    ) {
        return loanService.getLoanByTitleAndAuthor(bookTitle, author);
    }

    @GetMapping("/overdue")
    public List<Loan> getAllOverdueLoans() {
        return loanService.getAllOverdueLoans();
    }

    @GetMapping("/{loanId}/info")
    public String getSelectedInfo(
            @PathVariable Long loanId,
            @RequestParam String infoType
    ) {
        return loanService.getSelectedInfo(loanId, infoType);
    }

    @PutMapping("/{loanId}/return")
    public void returnLoan(
            @PathVariable Long loanId,
            @RequestParam Long userId,
            @RequestParam Long bookId
    ) {
        loanService.returnLoan(loanId, userId, bookId);
    }
}
