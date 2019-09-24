package com.endava.entities;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {

    private Long id;
    private BigDecimal funds;
    private User user;

    public Account(Long id, BigDecimal funds, User user) {
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
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFunds(BigDecimal funds) {
        this.funds = funds;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(funds, account.funds) &&
                Objects.equals(user, account.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, funds, user);
    }
}
