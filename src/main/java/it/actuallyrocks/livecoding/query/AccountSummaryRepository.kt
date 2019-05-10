package it.actuallyrocks.livecoding.query

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.rest.core.annotation.RepositoryRestResource

// Spring Data REST means a Spring Data JPA repository that publishes a HATEOAS REST API for working with AccountSummary. In reality,
// we would not have any state changing operations published for this entity though, as they get updated via Axon projections of
// aggregate state changes.
@RepositoryRestResource(path = "accounts")
interface AccountSummaryRepository : JpaRepository<AccountSummary, String>
