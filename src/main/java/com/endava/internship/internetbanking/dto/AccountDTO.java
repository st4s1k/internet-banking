package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AccountDTO {

    private Long id;

    private BigDecimal funds;

    @NonNull
    @NotNull(message = "Account user cannot be NULL.")
    private Long userId;
}
