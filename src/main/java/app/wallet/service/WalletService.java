package app.wallet.service;

import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.UUID;

@Service
public class WalletService {

    private static final String SMART_WALLET_SENDER_IDENTIFIER = "SMART WALLET PLATFORM";
    private static final String INACTIVE_WALLET_FAILURE_REASON = "Inactive wallet";
    private static final String TOP_UP_DESCRIPTION_FORMAT = "Top-up %.2f";

    private static final BigDecimal INITIAL_WALLET_BALANCE = new BigDecimal("20.00");
    private static final Currency DEFAULT_WALLET_CURRENCY = Currency.getInstance("EUR");

    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    @Autowired
    public WalletService(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    @Transactional
    public Transaction topUp(UUID walletId, BigDecimal topUpAmount) {

        Wallet wallet = getById(walletId);
        String transactionDescription = TOP_UP_DESCRIPTION_FORMAT.formatted(topUpAmount.doubleValue());

        if (wallet.getStatus() == WalletStatus.INACTIVE) {

            return transactionService.createNewTransaction(wallet.getOwner(),
                    SMART_WALLET_SENDER_IDENTIFIER,
                    wallet.getId().toString(),
                    topUpAmount,
                    wallet.getBalance(),
                    wallet.getCurrency(),
                    TransactionType.DEPOSIT,
                    TransactionStatus.FAILED,
                    transactionDescription,
                    INACTIVE_WALLET_FAILURE_REASON
            );
        }

        wallet.setBalance(wallet.getBalance().add(topUpAmount));
        wallet.setUpdatedOn(LocalDateTime.now());

        walletRepository.save(wallet);

        return transactionService.createNewTransaction(wallet.getOwner(),
                SMART_WALLET_SENDER_IDENTIFIER,
                wallet.getId().toString(),
                topUpAmount,
                wallet.getBalance(),
                wallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transactionDescription,
                null
        );
    }

    public Wallet createDefaultWallet(User user) {

        Wallet wallet = Wallet.builder()
                .owner(user)
                .status(WalletStatus.ACTIVE)
                .balance(INITIAL_WALLET_BALANCE)
                .currency(DEFAULT_WALLET_CURRENCY)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        return walletRepository.save(wallet);
    }

    private Wallet getById(UUID walletId) {

        return walletRepository.findById(walletId).orElseThrow(() -> new RuntimeException("Wallet by id [%s] was not found.".formatted(walletId)));
    }
}
