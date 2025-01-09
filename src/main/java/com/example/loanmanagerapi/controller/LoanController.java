package com.example.loanmanagerapi.controller;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.feign.AdminClient;
import com.example.loanmanagerapi.feign.UserClient;
import com.example.loanmanagerapi.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;



    @PostMapping("/create")
    public Loan createLoan(@RequestBody Loan loan) {

        return loanService.createLoan(loan);
    }


    @PutMapping("/{loanId}/extend")
    public void extendLoan(@PathVariable Long loanId) {
        // Get Admin permission
         loanService.extendLoan(loanId);
    }
    @GetMapping ("/PermissionGranted/{loanId}")
    public String extendLoan(@PathVariable String loanId) {
          loanService.permissionGranted(loanId);
          return "It worked";
    }

    @GetMapping("/{loanId}/days-left")
    public long getDaysLeft(@PathVariable Long loanId) {
        return loanService.getDaysLeft(loanId);
    }

    @GetMapping("/findByTitleAndAuthor")
    public List<Loan> getLoanByTitleAndAuthor(
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
    public String getSelectedInfo(@PathVariable Long loanId, @RequestParam String infoType) {
        return loanService.getSelectedInfo(loanId, infoType);
    }

    @PutMapping("/{loanId}/return")
    public void returnLoan(
            @PathVariable Long loanId
    ) {
        loanService.returnLoan(loanId);
    }
}
