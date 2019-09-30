package com.endava.dto;

import com.endava.entities.Account;
import com.endava.sevices.AccountService;
import com.fasterxml.jackson.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferDTO {

    @JsonIgnore
    @Autowired
    private AccountService accountService;

    @NotNull
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "source_id")
    private Account source;

    @NotNull
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "source_id")
    private Account destination;

    @NotEmpty
    private BigDecimal funds;

    public Account getSource() {
        return source;
    }

    public void setSource(Account source) {
        this.source = source;
    }

    public Account getDestination() {
        return destination;
    }

    public void setDestination(Account destination) {
        this.destination = destination;
    }

    public BigDecimal getFunds() {
        return funds;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    @JsonProperty("source_id")
    public void setSource(Long id) {
        source = accountService.findById(id).orElse(null);
    }

    @JsonProperty("destination_id")
    public void setDestination(Long id) {
        source = accountService.findById(id).orElse(null);
    }
}
