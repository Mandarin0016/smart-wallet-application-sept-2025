package app.wallet.service;

import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.service.TransactionService;
import app.user.model.User;
import app.wallet.model.Wallet;
import app.wallet.model.WalletStatus;
import app.wallet.repository.WalletRepository;
import app.web.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@Service
public class WalletService {

    private static final String SMART_WALLET_IDENTIFIER = "SMART WALLET PLATFORM";
    private static final String INACTIVE_WALLET_FAILURE_REASON = "Inactive wallet";
    private static final String INSUFFICIENT_FUNDS_FAILURE_REASON = "Insufficient funds";
    private static final String RECEIVER_WALLET_NOT_FOUND_FAILURE_REASON = "Receiver wallet was not found";
    private static final String TOP_UP_DESCRIPTION_FORMAT = "Top-up %.2f";
    private static final String TRANSFER_DESCRIPTION_FORMAT = "Transfer %.2f from %s to %s";

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
    public Transaction transfer(TransferRequest transferRequest) {

        Wallet senderWallet = getById(transferRequest.getFromWalletId());
        Optional<Wallet> receiverWalletOptional = walletRepository.findByOwnerUsername(transferRequest.getRecipientUsername())
                .stream()
                .filter(this::isActiveWallet)
                .findFirst();
        String transferDescription = TRANSFER_DESCRIPTION_FORMAT.formatted(transferRequest.getAmount(), senderWallet.getOwner().getUsername(), transferRequest.getRecipientUsername());

        if (receiverWalletOptional.isEmpty()) {
            return transactionService.createNewTransaction(senderWallet.getOwner(),
                    senderWallet.getId().toString(),
                    transferRequest.getRecipientUsername(),
                    transferRequest.getAmount(),
                    senderWallet.getBalance(),
                    senderWallet.getCurrency(),
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.FAILED,
                    transferDescription,
                    RECEIVER_WALLET_NOT_FOUND_FAILURE_REASON);
        }

        Transaction transaction = charge(senderWallet.getOwner(), transferRequest.getFromWalletId(), transferRequest.getAmount(), transferDescription);

        if (transaction.getStatus() == TransactionStatus.FAILED){
            return transaction;
        }

        Wallet receiverWallet = receiverWalletOptional.get();
        receiverWallet.setBalance(receiverWallet.getBalance().add(transferRequest.getAmount()));
        receiverWallet.setUpdatedOn(LocalDateTime.now());

        transactionService.createNewTransaction(receiverWallet.getOwner(),
                senderWallet.getId().toString(),
                receiverWallet.getId().toString(),
                transferRequest.getAmount(),
                receiverWallet.getBalance(),
                senderWallet.getCurrency(),
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCEEDED,
                transferDescription,
                null);

        return transaction;
    }

    @Transactional
    public Transaction charge(User user, UUID walletId, BigDecimal amount, String chargeDescription) {

        Wallet wallet = getById(walletId);
        String failureReason = null;
        boolean isFailedTransaction = false;

        Transaction transaction = Transaction.builder()
                .owner(user)
                .sender(walletId.toString())
                .receiver(SMART_WALLET_IDENTIFIER)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .description(chargeDescription)
                .createdOn(LocalDateTime.now())
                .currency(wallet.getCurrency())
                .build();

        if (!isActiveWallet(wallet)) {
            isFailedTransaction = true;
            failureReason = INACTIVE_WALLET_FAILURE_REASON;
        } else if (!hasEnoughFunds(wallet, amount)) {
            isFailedTransaction = true;
            failureReason = INSUFFICIENT_FUNDS_FAILURE_REASON;
        }

        if (isFailedTransaction) {
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(failureReason);
        } else {
            transaction.setStatus(TransactionStatus.SUCCEEDED);
            wallet.setBalance(wallet.getBalance().subtract(amount));
            wallet.setUpdatedOn(LocalDateTime.now());
            walletRepository.save(wallet);
        }

        transaction.setBalanceLeft(wallet.getBalance());

        return transactionService.upsert(transaction);
    }

    private boolean hasEnoughFunds(Wallet wallet, BigDecimal amount) {

        // A.compareTo(B)
        // result < 0 (A < B)
        // result = 0 (A = B)
        // result > 0 (A > B)

        BigDecimal a = wallet.getBalance();
        BigDecimal b = amount;

        return a.compareTo(b) > 0;
    }

    private boolean isActiveWallet(Wallet wallet) {

        return wallet.getStatus() == WalletStatus.ACTIVE;
    }

    @Transactional
    public Transaction topUp(UUID walletId, BigDecimal topUpAmount) {

        Wallet wallet = getById(walletId);
        String transactionDescription = TOP_UP_DESCRIPTION_FORMAT.formatted(topUpAmount.doubleValue());

        if (wallet.getStatus() == WalletStatus.INACTIVE) {

            return transactionService.createNewTransaction(wallet.getOwner(),
                    SMART_WALLET_IDENTIFIER,
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
                SMART_WALLET_IDENTIFIER,
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
