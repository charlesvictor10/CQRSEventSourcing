package dev.charles.query.entities;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import dev.charles.commonapi.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Account {
	@Id
	private String id;
	private double balance;
	@Enumerated(EnumType.STRING)
	private AccountStatus status;
	private String currency;
	@OneToMany(mappedBy = "account")
	private Collection<Operation> operations;
}
