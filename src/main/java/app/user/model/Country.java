package app.user.model;

public enum Country {
    BULGARIA("Bulgaria"),
    FRANCE("France"),
    GERMANY("Germany");

    private String displayName;

    Country(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
