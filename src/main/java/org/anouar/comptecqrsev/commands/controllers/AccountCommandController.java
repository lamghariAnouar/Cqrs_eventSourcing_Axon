package org.anouar.comptecqrsev.commands.controllers;

import lombok.AllArgsConstructor;
import org.anouar.comptecqrsev.commonApi.commands.CreateAccountCommand;
import org.anouar.comptecqrsev.commonApi.commands.CreditAccountCommand;
import org.anouar.comptecqrsev.commonApi.commands.DebitAccountCommand;
import org.anouar.comptecqrsev.commonApi.dtos.CreateAccountRequestDto;
import org.anouar.comptecqrsev.commonApi.dtos.CreditAccountRequestDto;
import org.anouar.comptecqrsev.commonApi.dtos.DebitAccountRequestDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor
public class AccountCommandController {
    private CommandGateway commandGateway;
    private EventStore eventStore;
    @PostMapping(path = "/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDto request){
                   //emettre une commande
               CompletableFuture<String> commandResponse= commandGateway.send(new CreateAccountCommand(
                       UUID.randomUUID().toString(),
                       request.getInitialBalance(),
                       request.getCurrency()
               ));
       return commandResponse ;
    }

    @PutMapping (path = "/credit")
    public CompletableFuture<String> creditAccount(@RequestBody CreditAccountRequestDto request){
        CompletableFuture<String> commandResponse= commandGateway.send(new CreditAccountCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
        return commandResponse ;
    }

    @PutMapping (path = "/debit")
    public CompletableFuture<String> creditAccount(@RequestBody DebitAccountRequestDto request){
        CompletableFuture<String> commandResponse= commandGateway.send(new DebitAccountCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
        return commandResponse ;
    }
// cette methode n'a rien a avoir avec l'event sourcing c'est juste pour traiter les exceptions
    @ExceptionHandler
    public ResponseEntity<String> exceptionsHandler(Exception exception){
        ResponseEntity<String> entity=new ResponseEntity<>(
                exception.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return entity;
    }

    @GetMapping(path = "/eventstore/{accountId}")
    public Stream eventStore(@PathVariable String accountId){
       return eventStore.readEvents(accountId).asStream();
    }
}
