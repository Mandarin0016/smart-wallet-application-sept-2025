package app.wallet;

import app.event.SuccessfulChargeEvent;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceWithdrawalTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private WalletService walletService;

    private User createUser(UUID id) {
        return User.builder()
                .id(id)
                .email("user" + id + "@mail.com")
                .username("user" + id)
                .build();
    }

    private Wallet createWallet(UUID id, User owner, BigDecimal balance, WalletStatus status) {
        return Wallet.builder()
                .id(id)
                .owner(owner)
                .balance(balance)
                .status(status)
                .currency(Currency.getInstance("EUR"))
                .build();
    }

    // ----------------------------------------------------------------------------------------
    // TEST 1 – Wallet inactive → FAILED
    // ----------------------------------------------------------------------------------------
    @Test
    void withdrawal_fails_whenWalletInactive() {

        UUID userId = UUID.randomUUID();

        User user = createUser(userId);
        Wallet wallet = createWallet(UUID.randomUUID(), user, new BigDecimal("100.00"), WalletStatus.INACTIVE);

        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
        when(transactionService.upsert(any())).thenAnswer(i -> i.getArgument(0));

        Transaction tx = walletService.withdrawal(user, wallet.getId(), new BigDecimal("10.00"), "Test");

        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.FAILED);
        assertThat(tx.getFailureReason()).isEqualTo("Inactive wallet");

        verify(walletRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
        verify(transactionService).upsert(tx);
    }

//    // ----------------------------------------------------------------------------------------
//    // TEST 2 – Insufficient funds → FAILED
//    // ----------------------------------------------------------------------------------------
//    @Test
//    void withdrawal_fails_whenInsufficientFunds() {
//
//        WalletRepository walletRepository = mock(WalletRepository.class);
//        TransactionService transactionService = mock(TransactionService.class);
//        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
//
//        WalletService service = buildService(walletRepository, transactionService, eventPublisher);
//
//        User user = createUser(1L);
//        Wallet wallet = createWallet(UUID.randomUUID(), user, new BigDecimal("5.00"), WalletStatus.ACTIVE);
//
//        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
//        when(transactionService.upsert(any())).thenAnswer(i -> i.getArgument(0));
//
//        Transaction tx = service.withdrawal(user, wallet.getId(), new BigDecimal("10.00"), "Test");
//
//        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.FAILED);
//        assertThat(tx.getFailureReason()).isEqualTo("Not enough funds");
//
//        verify(walletRepository, never()).save(any());
//        verify(eventPublisher, never()).publishEvent(any());
//    }
//
//    // ----------------------------------------------------------------------------------------
//    // TEST 3 – Not owned by user → FAILED
//    // ----------------------------------------------------------------------------------------
//    @Test
//    void withdrawal_fails_whenWalletNotOwnedByUser() {
//
//        WalletRepository walletRepository = mock(WalletRepository.class);
//        TransactionService transactionService = mock(TransactionService.class);
//        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
//
//        WalletService service = buildService(walletRepository, transactionService, eventPublisher);
//
//        User owner = createUser(99L);
//        User caller = createUser(1L);
//        Wallet wallet = createWallet(UUID.randomUUID(), owner, new BigDecimal("100.00"), WalletStatus.ACTIVE);
//
//        when(walletRepository.findById(wallet.getId())).thenReturn(Optional.of(wallet));
//        when(transactionService.upsert(any())).thenAnswer(i -> i.getArgument(0));
//
//        Transaction tx = service.withdrawal(caller, wallet.getId(), new BigDecimal("20.00"), "Test");
//
//        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.FAILED);
//        assertThat(tx.getFailureReason()).isEqualTo("You don't own this wallet");
//
//        verify(walletRepository, never()).save(any());
//        verify(eventPublisher, never()).publishEvent(any());
//    }
//
//    // ----------------------------------------------------------------------------------------
//    // TEST 4 – SUCCESS Path
//    // ----------------------------------------------------------------------------------------
//    @Test
//    void withdrawal_succeeds_whenValid() {
//
//        WalletRepository walletRepository = mock(WalletRepository.class);
//        TransactionService transactionService = mock(TransactionService.class);
//        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
//
//        WalletService service = buildService(walletRepository, transactionService, eventPublisher);
//
//        User user = createUser(1L);
//        UUID walletId = UUID.randomUUID();
//        BigDecimal initial = new BigDecimal("100.00");
//
//        Wallet wallet = createWallet(walletId, user, initial, WalletStatus.ACTIVE);
//
//        when(walletRepository.findById(walletId)).thenReturn(Optional.of(wallet));
//        when(transactionService.upsert(any())).thenAnswer(i -> i.getArgument(0));
//
//        BigDecimal amount = new BigDecimal("15.00");
//
//        Transaction tx = service.withdrawal(user, walletId, amount, "Valid withdrawal");
//
//        assertThat(tx.getStatus()).isEqualTo(TransactionStatus.SUCCEEDED);
//        assertThat(tx.getType()).isEqualTo(TransactionType.WITHDRAWAL);
//        assertThat(wallet.getBalance()).isEqualTo(initial.subtract(amount));
//        assertThat(tx.getBalanceLeft()).isEqualTo(wallet.getBalance());
//
//        verify(walletRepository, times(1)).save(wallet);
//        verify(eventPublisher, times(1)).publishEvent(any(SuccessfulChargeEvent.class));
//        verify(transactionService, times(1)).upsert(any(Transaction.class));
//    }
}