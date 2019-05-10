package it.actuallyrocks.livecoding;

import it.actuallyrocks.livecoding.domain.Account;
import org.axonframework.common.caching.Cache;
import org.axonframework.common.caching.WeakReferenceCache;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.modelling.command.Repository;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AxonCommandConfiguration {
    // Setup Axon event repository for the Account aggregate
    @Bean
    public Repository<Account> giftCardRepository(EventStore eventStore, Cache cache) {
        return EventSourcingRepository.builder(Account.class)
                                      .cache(cache)
                                      .eventStore(eventStore)
                                      .build();
    }

    // Setup a simple cache for Axon to use
    @Bean
    public Cache cache() {
        return new WeakReferenceCache();
    }
}
