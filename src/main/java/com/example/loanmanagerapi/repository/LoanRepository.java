package com.example.loanmanagerapi.repository;

import com.example.loanmanagerapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 1) Find Loan by bookTitle and author
    @Query("SELECT l FROM Loan l WHERE l.bookTitle = :bookTitle AND l.author = :author")
    Loan findLoanByTitleAndAuthor(String bookTitle, String author);

    // 2) Find all Overdue loans
    @Query("SELECT l FROM Loan l WHERE l.overdue = true")
    List<Loan> findAllOverdueLoans();
}
