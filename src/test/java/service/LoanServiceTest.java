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

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookClient bookClient;

    @Mock
    private UserClient userClient;

    private LoanService loanService;

    @BeforeEach
    void setUp() {
        // Inject mocked dependencies
        loanService = new LoanService(loanRepository, bookClient, userClient);
    }

    // -----------------------------
    // createLoanWithAdminCheck tests
    // -----------------------------
    @Test
    void createLoanWithAdminCheck_shouldThrowIfNotAdmin() {
        when(userClient.checkAdmin(1L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> loanService.createLoanWithAdminCheck(new Loan(), 1L, 100L),
                "Should throw if user is not admin");
    }

    @Test
    void createLoanWithAdminCheck_shouldThrowIfNoCopiesAvailable() {
        when(userClient.checkAdmin(1L)).thenReturn(true);
        when(bookClient.decrementCopies(100L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> loanService.createLoanWithAdminCheck(new Loan(), 1L, 100L),
                "Should throw if decrementCopies fails");
    }

    @Test
    void createLoanWithAdminCheck_shouldCreateLoanIfAdminAndCopiesAvailable() {
        when(userClient.checkAdmin(1L)).thenReturn(true);
        when(bookClient.decrementCopies(100L)).thenReturn(true);

        Loan sampleLoan = new Loan();
        sampleLoan.setId(999L);
        sampleLoan.setBookTitle("Test Title");
        sampleLoan.setCheckoutDate(LocalDate.now());

        when(loanRepository.save(any(Loan.class))).thenReturn(sampleLoan);

        Loan createdLoan = loanService.createLoanWithAdminCheck(new Loan(), 1L, 100L);

        assertNotNull(createdLoan, "Returned loan should not be null");
        assertEquals(999L, createdLoan.getId(), "Should match the mocked ID");
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    void createLoan_shouldSetDefaultValues() {
        Loan incomingLoan = new Loan();

        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan saved = invocation.getArgument(0);
            saved.setId(123L); // Simulate DB-generated ID
            return saved;
        });

        Loan createdLoan = loanService.createLoan(incomingLoan);

        assertNotNull(createdLoan.getId());
        assertEquals(LocalDate.now(), createdLoan.getCheckoutDate());
        assertEquals(LocalDate.now().plusWeeks(2), createdLoan.getExpiredDate());
        assertFalse(createdLoan.isOverdue());
        assertEquals(0, createdLoan.getExtensionCount());
        assertEquals("Active", createdLoan.getStatus());
    }

    // -----------------------------
    // extendLoan tests
    // -----------------------------
    @Test
    void extendLoan_shouldExtendIfAllowed() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setExpiredDate(LocalDate.now().plusDays(7));
        loan.setExtensionAllowed(true);
        loan.setExtensionCount(0);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        Loan result = loanService.extendLoan(1L);

        assertNotNull(result);
        assertEquals(1, result.getExtensionCount());
        assertEquals(LocalDate.now().plusDays(7 + 7), result.getExpiredDate(),
                "Expired date should be extended by 7 days");
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    void extendLoan_shouldNotExtendIfNotAllowed() {
        Loan loan = new Loan();
        loan.setId(2L);
        loan.setExpiredDate(LocalDate.now().plusDays(10));
        loan.setExtensionAllowed(false);
        loan.setExtensionCount(0);

        when(loanRepository.findById(2L)).thenReturn(Optional.of(loan));

        Loan result = loanService.extendLoan(2L);

        assertNotNull(result);
        assertEquals(0, result.getExtensionCount(),
                "Extension count should remain 0 if not allowed");
        assertEquals(LocalDate.now().plusDays(10), result.getExpiredDate(),
                "Expired date should not change if not allowed");
        verify(loanRepository, never()).save(loan);
    }

    @Test
    void extendLoan_shouldThrowIfLoanNotFound() {
        when(loanRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loanService.extendLoan(999L));
        assertTrue(ex.getMessage().contains("Loan not found with ID = 999"));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    // -----------------------------
    // getDaysLeft tests
    // -----------------------------
    @Test
    void getDaysLeft_shouldReturnCorrectDays() {
        Loan loan = new Loan();
        loan.setId(3L);
        loan.setExpiredDate(LocalDate.now().plusDays(5));

        when(loanRepository.findById(3L)).thenReturn(Optional.of(loan));

        long daysLeft = loanService.getDaysLeft(3L);
        assertEquals(5, daysLeft, "Should be exactly 5 days left");
    }

    @Test
    void getDaysLeft_shouldReturnNegativeIfOverdue() {
        Loan loan = new Loan();
        loan.setId(4L);
        loan.setExpiredDate(LocalDate.now().minusDays(2));

        when(loanRepository.findById(4L)).thenReturn(Optional.of(loan));

        long daysLeft = loanService.getDaysLeft(4L);
        assertTrue(daysLeft < 0, "Days left should be negative if loan is overdue");
    }

    @Test
    void getDaysLeft_shouldThrowIfLoanNotFound() {
        when(loanRepository.findById(123L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> loanService.getDaysLeft(123L));
        assertTrue(ex.getMessage().contains("Loan not found with ID = 123"));
    }
}
