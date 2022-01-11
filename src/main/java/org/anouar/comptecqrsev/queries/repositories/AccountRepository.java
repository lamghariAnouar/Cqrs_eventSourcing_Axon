package org.anouar.comptecqrsev.queries.repositories;

import org.anouar.comptecqrsev.queries.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,String> {
}
