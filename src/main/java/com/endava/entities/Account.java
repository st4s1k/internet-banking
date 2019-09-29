package com.endava.entities;

import com.endava.annotations.Column;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

public class Account implements Entity, Cloneable {

    public static class Builder {

        private Long id;
        private BigDecimal funds;
        private User user;

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

    public static String TABLE_NAME = "accounts";
    public static String ID_NAME = "id";

    @Column("id")
    private final Long id;
    @Column("funds")
    private final BigDecimal funds;
    @Column("user_id")
    private final User user;

    public Account() {
        id = null;
        funds = null;
        user = null;
    }

    private Account(Long id, BigDecimal funds, User user) {
        this.id = id;
        this.funds = funds;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getFunds() {
        return funds;
    }

    public User getUser() {
        return user.clone();
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getIdName() {
        return ID_NAME;
    }

    @Override
    public Object getIdValue() {
        return id;
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
}
