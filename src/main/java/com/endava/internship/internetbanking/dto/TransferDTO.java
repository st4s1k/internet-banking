package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferDTO {

    @NotNull
    private Long currentAccountId;

    @NotNull
    private Long targetAccountId;

    @NotNull
    private BigDecimal funds;
}
