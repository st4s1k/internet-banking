package com.endava.internship.internetbanking.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class AccountDTO {

    private Long id;

    @NotNull(message = "Funds amount cannot be NULL.")
    private BigDecimal funds;

    @NotNull(message = "Account user cannot be NULL.")
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @SuppressWarnings("ObjectComparison")
    @Override
    public boolean equals(Object o) {
        return o instanceof AccountDTO
                && (this == o || Objects.equals(userId, ((AccountDTO) o).userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

}
