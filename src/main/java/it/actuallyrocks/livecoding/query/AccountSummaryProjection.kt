package it.actuallyrocks.livecoding.query

import it.actuallyrocks.livecoding.domain.DepositEvent
import it.actuallyrocks.livecoding.domain.WithdrawEvent
import org.axonframework.eventhandling.EventHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.persistence.EntityManager

/**
 * Handle aggregate state changes, so we update the read model with current balance
 */
@Component
class AccountSummaryProjection(private val entityManager: EntityManager) {
    companion object {
        private val log = LoggerFactory.getLogger(AccountSummaryProjection::class.java)
    }

    @EventHandler
    fun handleDeposit(d: DepositEvent) {
        var summary = entityManager.find(AccountSummary::class.java, d.id)
        if (summary == null) {
            summary = AccountSummary(d.id, d.amount)
        } else {
            val summaryBalance: Int? = summary.balance
            val newBalance = if (summaryBalance == null) d.amount else d.amount.plus(summaryBalance)
            summary.balance = newBalance
        }
        entityManager.persist(summary)
        log.info("Updated read model for account {} after deposit: {}", d.id, summary.balance)
    }

    @EventHandler
    fun handleWithdraw(w: WithdrawEvent) {
        val summary : AccountSummary? = entityManager.find(AccountSummary::class.java, w.id)
        if (summary == null) {
            throw IllegalStateException("Attempt to withdraw from an account with no balance! " + w)
        }
        val summaryBalance = summary.balance
        val newBalance = summaryBalance - w.amount
        summary.balance = newBalance
        entityManager.persist(summary)
        log.info("Updated read model for account {} after withdrawal: {}", w.id, newBalance)
    }
}
