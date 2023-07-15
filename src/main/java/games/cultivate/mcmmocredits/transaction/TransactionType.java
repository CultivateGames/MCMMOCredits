package games.cultivate.mcmmocredits.transaction;

public enum TransactionType {
    ADD("credits-add", "credits-add-user"),
    SET("credits-set", "credits-set-user"),
    TAKE("credits-take", "credits-take-user"),
    REDEEM("redeem-sudo", "redeem-sudo-user"),
    PAY("credits-pay", "credits-pay-user");

    private final String userMessageKey;
    private final String messageKey;

    TransactionType(final String messageKey, final String userMessageKey) {
        this.messageKey = messageKey;
        this.userMessageKey = userMessageKey;
    }

    public String userMessageKey() {
        return this.userMessageKey;
    }

    public String messageKey() {
        return this.messageKey;
    }
}
