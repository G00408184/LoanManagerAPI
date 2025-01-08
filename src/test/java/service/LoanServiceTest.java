package service;

import com.example.loanmanagerapi.service.LoanService;


import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.feign.BookClient;
import com.example.loanmanagerapi.feign.UserClient;
import com.example.loanmanagerapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.loanmanagerapi.entity.Loan;
import com.example.loanmanagerapi.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan loan;

    @BeforeEach
    void setUp() {
        loan = new Loan();
        loan.setId(1L);
        loan.setCheckoutDate(LocalDate.now());
        loan.setExpiredDate(LocalDate.now().plusWeeks(2));
        loan.setStatus("Active");
    }

    @Test
    public void testCreateLoan() {
        when(loanRepository.save(any(Loan.class))).thenReturn(loan);

        Loan savedLoan = loanService.createLoan(loan);

        assertNotNull(savedLoan);
        assertEquals("Active", savedLoan.getStatus());
        assertEquals(0, savedLoan.getExtensionCount());
        assertEquals(loan.getExpiredDate(), savedLoan.getExpiredDate());
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    public void testGetDaysLeft() {
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        long daysLeft = loanService.getDaysLeft(1L);

        assertTrue(daysLeft > 0);
        assertEquals(14, daysLeft);  // 2 weeks from today
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetDaysLeftThrowsExceptionForInvalidLoanId() {
        when(loanRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> loanService.getDaysLeft(2L));

        assertEquals("Loan not found with ID = 2", exception.getMessage());
    }
}
