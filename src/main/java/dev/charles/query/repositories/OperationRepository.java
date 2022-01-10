package dev.charles.query.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.charles.query.entities.Operation;

public interface OperationRepository extends JpaRepository<Operation, Long> {

}
