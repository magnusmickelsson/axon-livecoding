package it.actuallyrocks.livecoding.domain

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.time.Instant

// Value objects for commands, events, transport
// Lets assume we're only dealing with accounts that have Int number withdraws/deposits, for simplicity's sake

// Commands - state changing operations
data class OpenAccountCommand(@TargetAggregateIdentifier val id: String, val owner: String)
data class DepositCommand(@TargetAggregateIdentifier val id: String, val amount: Int, val comment: String?, val source: String, val now: Instant?)
data class WithdrawCommand(@TargetAggregateIdentifier val id: String, val amount: Int, val comment: String?, val target: String, val now: Instant?)
data class CloseAccountCommand(@TargetAggregateIdentifier val id: String)

// Events - happen as a consequence of state changing operations on aggregates
data class AccountOpened(val id: String, val owner: String, val now: Instant)
data class AccountClosed(val id: String, val now: Instant)
data class DepositEvent(val id: String, val amount: Int, val comment: String?, val source: String, val now: Instant)
data class WithdrawEvent(val id: String, val amount: Int, val comment: String?, val target: String, val now: Instant)

// Value objects/Transport objects
data class Transaction(val transactionAmount: Int, val comment: String?, val counterPart: String)
