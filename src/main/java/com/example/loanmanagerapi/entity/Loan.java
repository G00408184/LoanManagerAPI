package com.example.loanmanagerapi.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;

@Data
@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bookTitle;
    private String author;          // <-- NEW FIELD

    private LocalDate checkoutDate;
    private LocalDate expiredDate;
    private String email;
    private boolean extensionAllowed;
    private int extensionCount;
    private boolean overdue;        // <-- Renamed from 'Overdue'
    private String status;
}
