package dev.charles.commands.controllers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.charles.commonapi.commands.CreatedAccountCommand;
import dev.charles.commonapi.dtos.CreateAccountRequestDTO;
import dev.charles.commonapi.dtos.CreditAccountRequestDTO;
import dev.charles.commonapi.dtos.DebitAccountRequestDTO;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {
	private CommandGateway commandGateway;
	private EventStore eventStore;
	
	@PostMapping(path = "/create")
	public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO createAccountRequestDTO) {
		CompletableFuture<String> commandResponse = commandGateway.send(new CreatedAccountCommand(
			UUID.randomUUID().toString(),
			createAccountRequestDTO.getInitialBalance(),
			createAccountRequestDTO.getCurrency()
		));
		return commandResponse;
	}
	
	@PutMapping(path = "/credit")
	public CompletableFuture<String> crediteAccount(@RequestBody CreditAccountRequestDTO creditAccountRequestDTO) {
		CompletableFuture<String> commandResponse = commandGateway.send(new CreditAccountRequestDTO(
			creditAccountRequestDTO.getAccountId(),
			creditAccountRequestDTO.getAmount(),
			creditAccountRequestDTO.getCurrency()
		));
		return commandResponse;
	}
	
	@PutMapping(path = "/debit")
	public CompletableFuture<String> debiteAccount(@RequestBody DebitAccountRequestDTO debitAccountRequestDTO) {
		CompletableFuture<String> commandResponse = commandGateway.send(new DebitAccountRequestDTO(
			debitAccountRequestDTO.getAccountId(),
			debitAccountRequestDTO.getAmount(),
			debitAccountRequestDTO.getCurrency()
		));
		return commandResponse;
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> exceptionHandler(Exception exception) {
		ResponseEntity<String> entity = new ResponseEntity<String>(
				exception.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR
		);
		return entity;
	}
	
	@GetMapping("/eventStore/{accountId}")
	public Stream<?> eventStore(@PathVariable String accountId) {
		return eventStore.readEvents(accountId).asStream();
	}
}
