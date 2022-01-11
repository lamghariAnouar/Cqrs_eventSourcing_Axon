package org.anouar.comptecqrsev.commonApi.dtos;

import lombok.Data;

@Data
public class CreditAccountRequestDto {
    private String accountId;
    private double amount;
    private String currency;
}
