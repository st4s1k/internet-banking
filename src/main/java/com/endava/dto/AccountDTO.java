package com.endava.dto;

import com.endava.entities.User;
import com.endava.sevices.UserService;
import com.fasterxml.jackson.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class AccountDTO {

    @JsonIgnore
    @Autowired
    private UserService userService;

    private Long id;

    @NotNull(message = "Funds amount cannot be NULL.")
    private BigDecimal funds;

    @NotNull(message = "Account user cannot be NULL.")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "user_id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("user_id")
    public void setUser(Long id) {
        this.user = userService.findById(id).orElse(null);
    }

    @SuppressWarnings("ObjectComparison")
    @Override
    public boolean equals(Object o) {
        return o instanceof AccountDTO
                && (this == o || Objects.equals(user, ((AccountDTO) o).user));
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

}
