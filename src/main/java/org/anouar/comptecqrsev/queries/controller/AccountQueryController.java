package org.anouar.comptecqrsev.queries.controller;

import lombok.extern.slf4j.Slf4j;
import org.anouar.comptecqrsev.commonApi.queries.GetAccountByIdQuery;
import org.anouar.comptecqrsev.commonApi.queries.GetAllAccountsQuery;
import org.anouar.comptecqrsev.queries.entities.Account;
import org.axonframework.messaging.responsetypes.ResponseType;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/query/account")
@Slf4j
public class AccountQueryController {
    private QueryGateway queryGateway;

    @GetMapping(path = "/allAccounts")
    public List<Account> accountList(){
        List<Account> response=queryGateway.query(new GetAllAccountsQuery(), ResponseTypes.multipleInstancesOf(Account.class)).join();
        return response;
    }
    @GetMapping(path = "/account/{id}")
    public Account getAccount(@PathVariable String id){
        return queryGateway.query(new GetAccountByIdQuery(id), ResponseTypes.instanceOf(Account.class)).join();

    }
}
