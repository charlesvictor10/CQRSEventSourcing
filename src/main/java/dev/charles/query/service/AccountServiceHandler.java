package dev.charles.query.service;

import java.util.Date;
import java.util.List;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.charles.commonapi.enums.TypeOperation;
import dev.charles.commonapi.events.AccountActivatedEvent;
import dev.charles.commonapi.events.AccountCreatedEvent;
import dev.charles.commonapi.events.AccountCreditedEvent;
import dev.charles.commonapi.events.AccountDebitedEvent;
import dev.charles.commonapi.queries.GetAccountByIdQuery;
import dev.charles.commonapi.queries.GetAllAccountsQuery;
import dev.charles.query.entities.Account;
import dev.charles.query.entities.Operation;
import dev.charles.query.repositories.AccountRepository;
import dev.charles.query.repositories.OperationRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class AccountServiceHandler {
	private AccountRepository accountRepository;
	private OperationRepository operationRepository;
	
	@EventHandler
	public void on(AccountCreatedEvent event) {
		Account account = new Account();
		account.setId(event.getId());
		account.setBalance(event.getInitialBalance());
		account.setStatus(event.getStatus());
		account.setCurrency(event.getCurrency());
		accountRepository.save(account);
	}
	
	@EventHandler
	public void on(AccountActivatedEvent event) {
		Account account = accountRepository.findById(event.getId()).get();
		account.setStatus(event.getStatus());
		accountRepository.save(account);
	}
	
	@EventHandler
	public void on(AccountDebitedEvent event) {
		Account account = accountRepository.findById(event.getId()).get();
		Operation operation = new Operation();
		operation.setAmount(event.getAmount());
		operation.setDate(new Date());
		operation.setAccount(account);
		operation.setType(TypeOperation.DEBIT);
		operationRepository.save(operation);
		account.setBalance(account.getBalance() - event.getAmount());
		accountRepository.save(account);
	}

	@EventHandler
	public void on(AccountCreditedEvent event) {
		Account account = accountRepository.findById(event.getId()).get();
		Operation operation = new Operation();
		operation.setAmount(event.getAmount());
		operation.setDate(new Date());
		operation.setAccount(account);
		operation.setType(TypeOperation.CREDIT);
		operationRepository.save(operation);
		account.setBalance(account.getBalance() + event.getAmount());
		accountRepository.save(account);
	}
	
	@QueryHandler
	public List<Account> on(GetAllAccountsQuery query) {
		return accountRepository.findAll();
	}
	
	@QueryHandler
	public Account on(GetAccountByIdQuery query) {
		return accountRepository.findById(query.getId()).get();
	}
}
