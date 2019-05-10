package it.actuallyrocks.livecoding.admin

import it.actuallyrocks.livecoding.domain.CloseAccountCommand
import it.actuallyrocks.livecoding.domain.DepositCommand
import it.actuallyrocks.livecoding.domain.OpenAccountCommand
import it.actuallyrocks.livecoding.domain.Transaction
import it.actuallyrocks.livecoding.domain.WithdrawCommand
import it.actuallyrocks.livecoding.domain.Account
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork
import org.axonframework.modelling.command.Repository
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountManager(private val commandGateway: CommandGateway, private val repository: Repository<Account>) {
    companion object {
        private val log = LoggerFactory.getLogger(AccountManager::class.java)
        private const val basePath = "/admin/account"
    }

    @PostMapping(basePath)
    fun openAccount(@RequestBody command: OpenAccountCommand) {
        log.info("Accepted command via REST {}", command)
        commandGateway.sendAndWait<OpenAccountCommand>(command)
    }

    @DeleteMapping("$basePath/{id}")
    fun closeAccount(@PathVariable("id") id: String) {
        val command = CloseAccountCommand(id)
        log.info("Accepted command via REST {}", command)
        commandGateway.sendAndWait<CloseAccountCommand>(command)
    }

    @PostMapping("$basePath/{id}/deposit")
    fun deposit(@PathVariable("id") id: String, @RequestBody command: DepositCommand) {
        log.info("Accepted command via REST {}", command)
        commandGateway.sendAndWait<DepositCommand>(command)
    }

    @PostMapping("$basePath/{id}/withdraw")
    fun withdraw(@PathVariable("id") id: String, @RequestBody command: WithdrawCommand) {
        log.info("Accepted command via REST {}", command)
        commandGateway.sendAndWait<WithdrawCommand>(command)
    }

    @GetMapping("$basePath/{id}/transactions")
    fun accountDetails(@PathVariable("id") id: String) : List<Transaction> {
        log.info("Loading account aggregate with id {}", id)
        val uow = DefaultUnitOfWork(null)
        uow.start()
        val aggregate = repository.load(id)
        val transactions = mutableListOf<Transaction>()
        aggregate.execute({transactions.addAll(it.transactions)})
        uow.commit()
        return transactions
    }

    @GetMapping("$basePath/{id}/balance")
    fun accountBalance(@PathVariable("id") id: String) : Int {
        log.info("Loading account aggregate with id {}", id)
        val uow = DefaultUnitOfWork(null)
        uow.start()
        val aggregate = repository.load(id)
        var balance = 0
        aggregate.execute({balance += it.balance})
        uow.commit()
        return balance
    }
}
