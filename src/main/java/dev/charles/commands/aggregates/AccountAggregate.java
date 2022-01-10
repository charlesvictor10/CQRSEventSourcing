package dev.charles.commands.aggregates;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import dev.charles.commonapi.commands.CreatedAccountCommand;
import dev.charles.commonapi.commands.CreditAccountCommand;
import dev.charles.commonapi.commands.DebitAccountCommand;
import dev.charles.commonapi.enums.AccountStatus;
import dev.charles.commonapi.events.AccountActivatedEvent;
import dev.charles.commonapi.events.AccountCreatedEvent;
import dev.charles.commonapi.events.AccountCreditedEvent;
import dev.charles.commonapi.events.AccountDebitedEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Aggregate
@NoArgsConstructor 
public class AccountAggregate {
	@AggregateIdentifier
	private String accountId;
	@Getter private double balance;
	@Getter private String currency;
	@Getter private AccountStatus status;
	
	@CommandHandler
	public AccountAggregate(CreatedAccountCommand createdAccountCommand) {
		if(createdAccountCommand.getInitialBalance() < 0)
			throw new RuntimeException("Impossible de créer un compte avec un solde négatif.");
		AggregateLifecycle.apply(new AccountCreatedEvent(
			createdAccountCommand.getId(),
			createdAccountCommand.getInitialBalance(),
			createdAccountCommand.getCurrency(),
			AccountStatus.CREATED
		));
	}
	
	@EventSourcingHandler
	public void on(AccountCreatedEvent event) {
		this.accountId = event.getId();
		this.balance = event.getInitialBalance();
		this.currency = event.getCurrency();
		this.status = AccountStatus.CREATED;		
		AggregateLifecycle.apply(new AccountActivatedEvent(
			event.getId(),
			AccountStatus.ACTIVETED
		));
	}
	
	public void on(AccountActivatedEvent event) {
		this.status = event.getStatus();
	}
	
	@CommandHandler
	public void handle(CreditAccountCommand creditAccountCommand) {
		if(creditAccountCommand.getAmount() < 0)
			throw new RuntimeException("Impossible de créditer un compte négatif");
		AggregateLifecycle.apply(new AccountCreditedEvent(
			creditAccountCommand.getId(),
			creditAccountCommand.getAmount(),
			creditAccountCommand.getCurrency()
		));
	}
	
	@EventSourcingHandler
	public void on(AccountCreditedEvent event) {
		this.balance += event.getAmount(); 
	}
	
	@CommandHandler
	public void handle(DebitAccountCommand debitAccountCommand) {
		if(debitAccountCommand.getAmount() < 0)
			throw new RuntimeException("Impossible de débiter un compte négatif");
		if(this.balance < debitAccountCommand.getAmount())
			throw new RuntimeException("Solde insuffisant");
		AggregateLifecycle.apply(new AccountDebitedEvent(
			debitAccountCommand.getId(),
			debitAccountCommand.getAmount(),
			debitAccountCommand.getCurrency()
		));
	}
	
	@EventSourcingHandler
	public void on(AccountDebitedEvent event) {
		this.balance += event.getAmount(); 
	}
}
