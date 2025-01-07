package com.example.loanmanagerapi.repository;

import com.example.loanmanagerapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // 1) Find all Loans by bookTitle and author (kept same method name)
    @Query("SELECT l FROM Loan l WHERE l.bookTitle = :bookTitle AND l.author = :author")
    List<Loan> countLoansByTitleAndAuthor(@Param("bookTitle") String bookTitle, @Param("author") String author);

    // 2) Find all Overdue loans (kept same method name)
    @Query("SELECT l FROM Loan l WHERE l.overdue = true")
    List<Loan> findAllOverdueLoans();

    // 3) Find Loans where expired date has passed and not yet marked overdue (kept same name)
    @Query("SELECT l FROM Loan l WHERE l.expiredDate < :currentDate AND l.overdue = false")
    List<Loan> findOverdueLoans(@Param("currentDate") LocalDate currentDate);
}
