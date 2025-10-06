package app.subscription.model;

public enum SubscriptionPeriod {
    MONTHLY("Monthly (1 month)"),
    YEARLY("Yearly (12 months)");

    private String displayName;

    SubscriptionPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
