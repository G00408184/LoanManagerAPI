package com.example.loanmanagerapi.service;

import com.example.loanmanagerapi.MessageQueue.Message;
import com.example.loanmanagerapi.MessageQueue.MessageProducer;
import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.feign.BookClient;
import com.example.loanmanagerapi.feign.UserClient;
import com.example.loanmanagerapi.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LoanService {
    @Autowired

    private final LoanRepository loanRepository;
    @Autowired
    private final BookClient bookClient;

    private final MessageProducer messageProducer;


    public LoanService(LoanRepository loanRepository,
                       BookClient bookClient, MessageProducer messageProducer) {
        this.loanRepository = loanRepository;
        this.bookClient = bookClient;
        this.messageProducer = messageProducer;
    }

    public Loan createLoan(Loan loan) {

        // if (!userClient.checkUserByID(loan.getUserId())) {
        //   throw new RuntimeException("User not found");
        // }

        // loan.setEmail("herewego@gmail.com");
        //loan.setUserId(160005);
        loan.setCheckoutDate(LocalDate.now());
        loan.setExpiredDate(LocalDate.now().plusWeeks(2));
        loan.setOverdue(false);
        loan.setExtensionCount(0);
        loan.setStatus("Active");
        // loan.setBookTitle("The Great Gatsby");
        // loan.setAuthor("F. Scott Fitzgerald");


        // boolean success = bookClient.decreaseCopies(loan.getBookTitle(), loan.getAuthor());
        //  if (!success) {
        //    throw new RuntimeException("Failed to decrement book copies for book ");
        //  }
        System.out.println(loan);

        return loanRepository.save(loan);
    }

    public void extendLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));

        // 1) get admin permission by adding it to the queue, send in the loan ID
        Message message = new Message();
        message.setId(String.valueOf(System.currentTimeMillis()));
        message.setContent("ExtendLoan");
        message.setTimestamp(LocalDateTime.now().toString());
        message.setType(loanId.toString());
        message.setEmail(loan.getEmail());

        messageProducer.sendMessage(message);

    }

    public long getDaysLeft(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));
        return ChronoUnit.DAYS.between(LocalDate.now(), loan.getExpiredDate());
    }

    public List<Loan> getLoanByTitleAndAuthor(String bookTitle, String author) {
        return loanRepository.countLoansByTitleAndAuthor(bookTitle, author);
    }

    public List<Loan> getAllOverdueLoans() {
        return loanRepository.findAllOverdueLoans();
    }

    public String getSelectedInfo(Long loanId, String infoType) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));
        return switch (infoType.toLowerCase()) {
            case "checkoutdate" -> "Checkout Date: " + loan.getCheckoutDate();
            case "expireddate" -> "Expired Date: " + loan.getExpiredDate();
            case "status" -> "Status: " + loan.getStatus();
            default -> "Unknown info type. Please choose 'checkoutDate', 'expiredDate', or 'status'.";
        };
    }

    public void returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + loanId));

        // 1) Increment copies
        boolean success = bookClient.incrementCopies(loan.getBookTitle(), loan.getAuthor());
        if (!success) {
            throw new RuntimeException("Could not increment copies for book " + loan.getBookTitle());
        }

        // 3) Delete the loan
        loanRepository.deleteById(loanId);
    }

    public Loan permissionGranted(String loanID) {
        Long id = Long.parseLong(loanID);
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Loan not found with ID = " + id));
            loan.setExpiredDate(loan.getExpiredDate().plusWeeks(1));
            loan.setExtensionCount(loan.getExtensionCount() + 1);
            loanRepository.save(loan);
        return loan;
    }

    @Transactional
    public void CheckAndUpdateOverdueLoans() {

        LocalDate currentDate = LocalDate.now();

        List<Loan> overdueLoans = loanRepository.findOverdueLoans(currentDate);

        for (Loan loan : overdueLoans) {
            loan.setOverdue(true);
            loan.setStatus("Overdue");

            Message message = new Message();
            message.setId(String.valueOf(System.currentTimeMillis()));
            message.setContent("SendEmailToUser");
            message.setTimestamp(LocalDateTime.now().toString());
            message.setType("SendEmailToUser");
            message.setEmail(loan.getEmail());

            messageProducer.sendMessage(message);
        }

       loanRepository.saveAll(overdueLoans);
        System.out.println("Overdue loans checked and updated successfully.");

    }
}
