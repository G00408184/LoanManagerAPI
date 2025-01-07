package com.example.loanmanagerapi.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LoanScheduler {

    private final LoanService loanService;

    public LoanScheduler(LoanService loanService) {
        this.loanService = loanService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runOverdueCheck() {
        loanService.CheckAndUpdateOverdueLoans();
        System.out.println("Daily overdue loan check completed.");
    }

}
