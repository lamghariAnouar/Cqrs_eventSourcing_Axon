package org.anouar.comptecqrsev.queries.repositories;

import org.anouar.comptecqrsev.queries.entities.Account;
import org.anouar.comptecqrsev.queries.entities.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation,Long> {
}
