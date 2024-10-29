package com.example.loanmanagerapi.repository;

import com.example.loanmanagerapi.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    // Additional query methods if needed
}
