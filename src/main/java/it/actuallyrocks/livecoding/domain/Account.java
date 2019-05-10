package it.actuallyrocks.livecoding.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * Axon aggregate for the simple bank account we've modelled. Aggregates should usually never be fetched, one should only
 * invoke state change operations (commands) on it, then project state changes to update the read model, which is what you read from.
 */
@Aggregate
public class Account {
    private static final Logger log = LoggerFactory.getLogger(Account.class);
    @AggregateIdentifier
    private String id;
    private int balance = 0;
    private List<Transaction> transactions = new LinkedList<>();
    private String owner;
    private Boolean open;
    private Instant lastUpdated;

    protected Account() {
    }

    // Command handlers accept the state changing commands, do validation and apply business logic, then pass on resulting events.
    // Commands are NOT stored in event repository, events are.

    @CommandHandler
    public Account(OpenAccountCommand openAccountCommand) {
        log.info("COMMAND - open account: {}", openAccountCommand);
        if (id != null || owner != null || transactions.size() != 0 || balance != 0 || open != null) {
            throw new IllegalStateException("Attempt to open an already existing account, current owner: " + owner + ". Command: " + openAccountCommand);
        }
        // Play the event so state gets updated and saved in event repository
        apply(new AccountOpened(openAccountCommand.getId(), openAccountCommand.getOwner(), Instant.now()));
    }

    @CommandHandler
    public void handle(CloseAccountCommand closeAccountCommand) {
        log.info("COMMAND - Close account: {}", closeAccountCommand);

        if (!open) {
            throw new IllegalStateException("Attempt to close a non-open account! Command: " + closeAccountCommand);
        }
        apply(new AccountClosed(closeAccountCommand.getId(), Instant.now()));
    }

    @CommandHandler
    public void handle(WithdrawCommand withdrawal) {
        log.info("COMMAND - Withdrawal: {}", withdrawal);
        validateOpenAccount(withdrawal);
        if (withdrawal.getAmount() < 0) {
            throw new IllegalArgumentException("Can't withdraw a negative amount: " + withdrawal.getAmount());
        }
        // If a command was created earlier and prevented from being run, we can still use its timestamp so we know when operation was created
        Instant now = withdrawal.getNow() == null ? Instant.now() : withdrawal.getNow();
        apply(new WithdrawEvent(withdrawal.getId(), withdrawal.getAmount(), withdrawal.getComment(), withdrawal.getTarget(),
                now));
    }

    @CommandHandler
    public void handle(DepositCommand depositCommand) {
        log.info("COMMAND - Deposit: {}", depositCommand);
        validateOpenAccount(depositCommand);
        if (depositCommand.getAmount() < 0) {
            throw new IllegalArgumentException("Can't deposit a negative amount: " + depositCommand.getAmount());
        }
        Instant now = depositCommand.getNow() == null ? Instant.now() : depositCommand.getNow();
        apply(new DepositEvent(depositCommand.getId(), depositCommand.getAmount(), depositCommand.getComment(), depositCommand.getSource(),
                now));
    }

    // Event handlers accept the events that modify state (events also get saved in event repository implicitly by Axon)

    @EventHandler
    public void accept(AccountOpened event) {
        this.id = event.getId();
        this.open = true;
        this.owner = event.getOwner();
        log.info("EVENT - Account opened: {}", event);
        setLastUpdated(event.getNow());
    }

    @EventHandler
    public void accept(DepositEvent event) {
        log.info("EVENT - Account deposit: {}", event);
        setLastUpdated(event.getNow());
        transactions.add(new Transaction(event.getAmount(), event.getComment(), event.getSource()));
        updateBalance();
    }

    @EventHandler
    public void accept(WithdrawEvent event) {
        log.info("EVENT - Account withdrawal: {}", event);
        setLastUpdated(event.getNow());
        transactions.add(new Transaction(-1 * event.getAmount(), event.getComment(), event.getTarget()));
        updateBalance();
    }

    @EventHandler
    public void accept(AccountClosed event) {
        this.open = false;
        setLastUpdated(event.getNow());
        log.info("EVENT - Account closed: {}", event);
    }

    // State accessors

    public String getId() {
        return id;
    }

    public int getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getOwner() {
        return owner;
    }

    public Boolean getOpen() {
        return open;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    // Private utility methods

    private void updateBalance() {
        balance = transactions.stream().mapToInt(Transaction::getTransactionAmount).sum();
    }

    private void validateOpenAccount(Object o) {
        if (!open) {
            throw new IllegalStateException("Command run against a closed account: " + o);
        }
    }

    private void setLastUpdated(final Instant instant) {
        this.lastUpdated = instant;
    }
}
