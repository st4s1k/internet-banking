package com.endava.internship.internetbanking.dto;

import java.math.BigDecimal;

public interface ITransferDTO {

    BigDecimal getFunds();

    Long getCurrentAccountId();

    Long getTargetAccountId();

    Long getSourceId();

    Long getDestinationId();
}
