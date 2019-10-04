package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountDTO {

    private Long id;

    private BigDecimal funds;

    @NotNull(message = "Account user cannot be NULL.")
    private Long userId;
}
