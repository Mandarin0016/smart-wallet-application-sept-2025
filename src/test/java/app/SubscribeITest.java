package app;

import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionStatus;
import app.subscription.model.SubscriptionType;
import app.subscription.service.SubscriptionService;
import app.transaction.model.Transaction;
import app.transaction.model.TransactionStatus;
import app.user.model.Country;
import app.user.model.User;
import app.user.service.UserService;
import app.wallet.model.Wallet;
import app.wallet.repository.WalletRepository;
import app.wallet.service.WalletService;
import app.web.dto.RegisterRequest;
import app.web.dto.UpgradeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@SpringBootTest
public class SubscribeITest {

    @Autowired
    private UserService userService;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private WalletService walletService;

    // What feature to test?
    // Upgrade to Premium Subscription

    @Test
    void subscribeToPremium_happyPath() {

        // Given
        RegisterRequest registerRequest = new RegisterRequest("Vik123", "123123", Country.BULGARIA);
        User registeredUser = userService.register(registerRequest);
        Wallet wallet = registeredUser.getWallets().get(0);
        UpgradeRequest upgradeRequest = new UpgradeRequest(SubscriptionPeriod.MONTHLY, wallet.getId());

        // When
        Transaction transaction = subscriptionService.upgrade(registeredUser, upgradeRequest, SubscriptionType.PREMIUM);

        // Then
        // transaction is successful
        assertEquals(TransactionStatus.SUCCEEDED, transaction.getStatus());
        // wallet balance decreases
        Wallet updatedWallet = walletService.getById(wallet.getId());
        assertNotEquals(wallet.getBalance(), updatedWallet.getBalance());
        // currently active subscription is set to COMPLETED
        // newly Active Subscription is created for this user
        List<Subscription> allUserSubscriptions = subscriptionService.getAllByOwnerId(registeredUser.getId());
        assertThat(allUserSubscriptions).hasSize(2);
        Subscription completedSubscription = allUserSubscriptions.stream().filter(subscription -> subscription.getStatus() == SubscriptionStatus.COMPLETED).findFirst().get();
        Subscription activeSubscription = allUserSubscriptions.stream().filter(subscription -> subscription.getStatus() == SubscriptionStatus.ACTIVE).findFirst().get();
        assertNotNull(completedSubscription);
        assertNotNull(activeSubscription);
        assertEquals(SubscriptionType.DEFAULT, completedSubscription.getType());
        assertEquals(SubscriptionType.PREMIUM, activeSubscription.getType());
    }
}
