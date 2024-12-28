package com.example.loanmanagerapi.service;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.feign.BookClient;
import com.example.loanmanagerapi.feign.UserClient;
import com.example.loanmanagerapi.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final BookClient bookClient;
    private final UserClient userClient;

    public LoanService(LoanRepository loanRepository,
                       BookClient bookClient,
                       UserClient userClient) {
        this.loanRepository = loanRepository;
        this.bookClient = bookClient;
        this.userClient = userClient;
    }

    public Loan createLoan(Loan loan) {
        loan.setCheckoutDate(LocalDate.now());
        loan.setExpiredDate(LocalDate.now().plusWeeks(2));
        loan.setOverdue(false);
        loan.setExtensionCount(0);
        loan.setStatus("Active");
        return loanRepository.save(loan);
    }

    public Loan createLoanWithAdminCheck(Loan loan, Long userId, Long bookId) {
        // 1) Check admin permission via UserClient
        boolean isAdmin = userClient.checkAdmin(userId);
        if (!isAdmin) {
            throw new RuntimeException("User " + userId + " is not an admin.");
        }

        // 2) Decrement book copies via BookClient
        boolean success = bookClient.decrementCopies(bookId);
        if (!success) {
            throw new RuntimeException("Failed to decrement book copies for book " + bookId);
        }

        // 3) Create the loan
        return createLoan(loan);
    }

    public Loan extendLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));

        if (loan.isExtensionAllowed()) {
            loan.setExpiredDate(loan.getExpiredDate().plusWeeks(1));
            loan.setExtensionCount(loan.getExtensionCount() + 1);
            loanRepository.save(loan);
        }
        return loan;
    }

    public long getDaysLeft(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));
        return ChronoUnit.DAYS.between(LocalDate.now(), loan.getExpiredDate());
    }

    public Loan getLoanByTitleAndAuthor(String bookTitle, String author) {
        return loanRepository.findLoanByTitleAndAuthor(bookTitle, author);
    }

    public List<Loan> getAllOverdueLoans() {
        return loanRepository.findAllOverdueLoans();
    }

    public String getSelectedInfo(Long loanId, String infoType) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));
        switch (infoType.toLowerCase()) {
            case "checkoutdate":
                return "Checkout Date: " + loan.getCheckoutDate();
            case "expireddate":
                return "Expired Date: " + loan.getExpiredDate();
            case "status":
                return "Status: " + loan.getStatus();
            default:
                return "Unknown info type. Please choose 'checkoutDate', 'expiredDate', or 'status'.";
        }
    }

    public void returnLoan(Long loanId, Long userId, Long bookId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));

        // 1) Increment copies
        boolean success = bookClient.incrementCopies(bookId);
        if (!success) {
            throw new RuntimeException("Could not increment copies for book " + bookId);
        }

        // 2) Record the return in User microservice
        userClient.recordReturn(userId, loanId);

        // 3) Delete the loan
        loanRepository.deleteById(loanId);
    }
}
