package dev.charles.query.controllers;

import java.util.List;

import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.charles.commonapi.queries.GetAccountByIdQuery;
import dev.charles.commonapi.queries.GetAllAccountsQuery;
import dev.charles.query.entities.Account;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(path = "/query/accounts")
@AllArgsConstructor
public class AccountQueryController {
	private QueryGateway queryGateway;

	@GetMapping("/allAccounts")
	public List<Account> accountList() {
		List<Account> response = queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(Account.class)).join();
		return response;
	}
	
	@GetMapping("/accountById/{id}")
	public Account getOneAccount(@PathVariable String id) {
		return queryGateway.query(new GetAccountByIdQuery(id), ResponseTypes.instanceOf(Account.class)).join();
	}
}
