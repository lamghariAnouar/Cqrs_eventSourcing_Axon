package org.anouar.comptecqrsev.commands.aggregate;

import org.anouar.comptecqrsev.commonApi.commands.CreateAccountCommand;
import org.anouar.comptecqrsev.commonApi.commands.CreditAccountCommand;
import org.anouar.comptecqrsev.commonApi.commands.DebitAccountCommand;
import org.anouar.comptecqrsev.commonApi.enums.AccountStatus;
import org.anouar.comptecqrsev.commonApi.events.AccountActivatedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountCreatedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountCreditedEvent;
import org.anouar.comptecqrsev.commonApi.events.AccountDebitedEvent;
import org.anouar.comptecqrsev.commonApi.exceptions.BalanceNotSufficientException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAgregate {
    @AggregateIdentifier  //l'iddentifiant de l'aggregat
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAgregate() {
        //Required by axon
        //lorsque axon restitue l'agregat a partir de l'event store il utilise le consttucteur sans parramettres
    }

    @CommandHandler // quand vous mettez commandHandler ca veut dire faite un subscribe sur le bus de command
    //==j'ecoute des quel il ya une commande de type CreateAccountCommand qui arrive axon va instancier cette aggregatat
    //on applique la logique metier(Fonction de decesion)
    public AccountAgregate(CreateAccountCommand createAccountCommand) {
          if(createAccountCommand.getInitialBalance()<0) throw new RuntimeException("impossible......");
          //ok
        //pour emettre un evenement
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(),
                status));
    }
    @EventSourcingHandler
    //paylod:contenu de l'évenement
    public void on(AccountCreatedEvent event){
        this.accountId=event.getId();
        this.balance=event.getInitialBalance();
        this.status=AccountStatus.CREATATED;
        this.currency=event.getCurrency();
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED
        ));

    }
    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.status=event.getStatus();
    }
    @CommandHandler
    //quand la commande est emise sur le bus de commande,cette fonction ve s'éxecuter
    public void handle(CreditAccountCommand command){
        if(command.getAmount()<0) throw new RuntimeException("Amount should not be negative");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency(),
                AccountStatus.CREATATED));
    }
    @EventSourcingHandler
    public void on(AccountCreditedEvent event){
        this.balance+=event.getAmount();
    }

    @CommandHandler
    public void handle(DebitAccountCommand command){
        if(command.getAmount()<0) throw new RuntimeException("Amount should not be negative");
        if(this.balance<command.getAmount()) throw new BalanceNotSufficientException("Ballance Not Sufficient ==>" +this.balance);
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountDebitedEvent event){
        this.balance-=event.getAmount();
    }
}
