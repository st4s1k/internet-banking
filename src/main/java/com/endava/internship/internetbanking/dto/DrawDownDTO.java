package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DrawDownDTO implements ITransferDTO {

    @NotNull
    private Long currentAccountId;

    @NotNull
    private Long targetAccountId;

    @NotNull
    private BigDecimal funds;

    @Override
    public Long getSourceId() {
        return getTargetAccountId();
    }

    @Override
    public Long getDestinationId() {
        return getCurrentAccountId();
    }
}
