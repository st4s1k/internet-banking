package com.endava.internship.internetbanking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransferDTO {

    @NotNull(message = "${http.transfer_operation.fail.bad_current_account}")
    private Long currentAccountId;

    @NotNull(message = "${http.transfer_operation.fail.bad_target_account}")
    private Long targetAccountId;

    @NotNull(message = "${http.transfer_operation.fail.bad_transfer_amount}")
    private BigDecimal funds;
}
