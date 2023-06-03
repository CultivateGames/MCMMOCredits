package games.cultivate.mcmmocredits.transaction;

import games.cultivate.mcmmocredits.user.CommandExecutor;
import games.cultivate.mcmmocredits.user.User;

import java.util.Optional;

/**
 * Represents a transaction in which the executor gives credits to the target.
 */
public final class PayTransaction implements Transaction {
    private final User executor;
    private final User target;
    private final int amount;

    /**
     * Constructs the object.
     *
     * @param executor The executor of the transaction.
     * @param target   The user to apply the transaction to.
     * @param amount   The amount to use.
     */
    private PayTransaction(final User executor, final User target, final int amount) {
        this.executor = executor;
        this.target = target;
        this.amount = amount;
    }

    /**
     * Creates a transaction using the provided information.
     *
     * @param executor The executor of the transaction.
     * @param target   The user to apply the transaction to.
     * @param amount   The amount to use.
     * @return The transaction.
     */
    public static PayTransaction of(final User executor, final User target, final int amount) {
        return new PayTransaction(executor, target, amount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionResult execute() {
        int execResult = BasicTransactionType.TAKE.apply(this.executor.credits(), this.amount);
        int targetResult = BasicTransactionType.ADD.apply(this.target.credits(), this.amount);
        return TransactionResult.of(this, this.executor.withCredits(execResult), this.target.withCredits(targetResult));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FailureReason> executable() {
        try {
            int execBalance = BasicTransactionType.TAKE.apply(this.executor.credits(), this.amount);
            int targetBalance = BasicTransactionType.ADD.apply(this.target.credits(), this.amount);
            return execBalance < 0 || targetBalance < 0 ? Optional.of(FailureReason.NOT_ENOUGH_CREDITS) : Optional.empty();
        } catch (ArithmeticException e) {
            //TODO: new failure reasons
            return Optional.of(FailureReason.NOT_ENOUGH_CREDITS);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int amount() {
        return this.amount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelfTransaction() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessageKey() {
        return "credits-pay";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserMessageKey() {
        return "credits-pay-user";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CommandExecutor executor() {
        return this.executor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User target() {
        return this.target;
    }
}
