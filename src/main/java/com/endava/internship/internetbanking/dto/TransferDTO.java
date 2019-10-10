package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferDTO {

    private Long currentAccountId;

    private Long targetAccountId;

    private BigDecimal funds;
}
