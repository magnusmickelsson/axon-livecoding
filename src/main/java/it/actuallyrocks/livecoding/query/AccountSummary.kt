package it.actuallyrocks.livecoding.query

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery

/**
 * Read model entity
 */
@Entity
@NamedQueries(
    NamedQuery(name = "AccountSummary.fetch",
        query = "SELECT c FROM AccountSummary c WHERE c.id LIKE CONCAT(:idStartsWith, '%') ORDER BY c.id"),
    NamedQuery(name = "AccountSummary.count",
        query = "SELECT COUNT(c) FROM AccountSummary c WHERE c.id LIKE CONCAT(:idStartsWith, '%')"))
data class AccountSummary(@Id var id: String, var balance: Int) {
    // Needed by JPA
    constructor() : this("", 0)
}