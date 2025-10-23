package app.utils;

import app.subscription.model.SubscriptionType;
import app.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WalletUtils {

    public static boolean isEligibleToUnlockNewWallet(User user) {

        // DEFAULT - не може

        // Кога може да си отключи нов потфейл?
        // PREMIUM и потфейлите са по-малки от 2
        // ULTIMATE и потфейлите са по-малки от 3

        SubscriptionType subscriptionType = user.getSubscriptions().get(0).getType();
        int walletsSize = user.getWallets().size();

        return (subscriptionType == SubscriptionType.PREMIUM && walletsSize < 2) || (subscriptionType == SubscriptionType.ULTIMATE && walletsSize < 3);
    }
}
