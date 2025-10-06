package app.transaction.service;

import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.transaction.model.TransactionType;
import app.transaction.repository.TransactionRepository;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction upsert(Transaction transaction) {

        return transactionRepository.save(transaction);
    }

    public Transaction createNewTransaction(User owner, String sender, String receiver, BigDecimal amount, BigDecimal balanceLeft, Currency currency, TransactionType type, TransactionStatus status, String description, String failureReason) {

        Transaction transaction = Transaction.builder()
                .owner(owner)
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .balanceLeft(balanceLeft)
                .currency(currency)
                .type(type)
                .status(status)
                .description(description)
                .failureReason(failureReason)
                .createdOn(LocalDateTime.now())
                .build();

        return transactionRepository.save(transaction);
    }

    public Transaction getById(UUID id) {

        return transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found."));
    }

    public List<Transaction> getAllByUserId(UUID userId) {

        return transactionRepository.findAllByOwnerId(userId);
    }
}
