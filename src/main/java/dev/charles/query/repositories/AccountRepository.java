package dev.charles.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.charles.query.entities.Account;

public interface AccountRepository extends JpaRepository<Account, String> {

}
