package org.anouar.comptecqrsev.queries.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anouar.comptecqrsev.commonApi.enums.AccountStatus;
import org.anouar.comptecqrsev.commonApi.enums.OperationType;
import org.anouar.comptecqrsev.commonApi.events.AccountActivatedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountCreatedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountCreditedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountDebitedEvent;
import org.anouar.comptecqrsev.commonApi.queries.GetAccountByIdQuery;
import org.anouar.comptecqrsev.commonApi.queries.GetAllAccountsQuery;
import org.anouar.comptecqrsev.queries.entities.Account;
import org.anouar.comptecqrsev.queries.entities.Operation;
import org.anouar.comptecqrsev.queries.repositories.AccountRepository;
import org.anouar.comptecqrsev.queries.repositories.OperationRepository;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

@Service @AllArgsConstructor
@Slf4j
@Transactional
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    //c'est ici ou on ecoute les evenements qui sont dans le bus d'évenments
    @EventHandler
    public void on(AccountCreatedEvent event){
        log.info("*******************************************");
        log.info("AccountCreatedEvent received");
        Account account=new Account();
        account.setId(event.getId());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }
    @EventHandler
    public void on(AccountActivatedEvent event){
        log.info("******************************");
        log.info("AccountActivatedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountDebitedEvent event){
        log.info("******************************");
        log.info("AccountDebitedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
        Operation operation=new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());/*a ne pas faire car la date de l'operation
         doit etre l'operation doit correspondre
        au date de production de l'évenement*/
        operation.setType(OperationType.DEBIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()-event.getAmount());
        accountRepository.save(account);
    }

    @EventHandler
    public void on(AccountCreditedEvent event){
        log.info("******************************");
        log.info("AccountCreditedEvent received");
        Account account=accountRepository.findById(event.getId()).get();
        Operation operation=new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());/*a ne pas faire car la date de l'operation
         doit etre l'operation doit correspondre
        au date de production de l'évenement*/
        operation.setType(OperationType.CREDIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());
        accountRepository.save(account);
    }

    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }

    @QueryHandler
    public Account on(GetAccountByIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }
}
