package com.endava.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "funds")
    private BigDecimal funds;

    @Column(name = "user_id")
    private User user;

    public Account() {
        id = null;
        funds = null;
        user = null;
    }

    private Account(Long id, User user) {
        this.id = id;
        this.funds = BigDecimal.ZERO;
        this.user = user;
    }

    private Account(Long id, BigDecimal funds, User user) {
        this.id = id;
        this.funds = funds;
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getFunds() {
        return funds;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Account
                && (this == o || Objects.equals(user, ((Account) o).user));
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public Account clone() {
        return new Builder().setId(id).setFunds(funds).setUser(user).build();
    }

    public static class Builder {

        private Long id;
        private BigDecimal funds;
        private User user;

        private Builder() {
        }

        public Builder setId(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public Builder setFunds(@NotNull BigDecimal funds) {
            this.funds = funds;
            return this;
        }

        public Builder setUser(@NotNull User user) {
            this.user = user;
            return this;
        }

        public Account build() {
            return new Account(this.id, this.funds, this.user);
        }

    }
}
