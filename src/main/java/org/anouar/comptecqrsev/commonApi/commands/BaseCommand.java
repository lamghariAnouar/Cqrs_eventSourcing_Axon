package org.anouar.comptecqrsev.commonApi.commands;

import lombok.Getter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public abstract class BaseCommand<T> {
    @TargetAggregateIdentifier //ca represente l id de l'agregat sur laquel on applique la commande(dans notre exemple l'agregat represente  le compte
    @Getter private T id;// les commandes et les evenements sont des objets immuables donc pas setters

    public BaseCommand(T id) {
        this.id = id;
    }
}
