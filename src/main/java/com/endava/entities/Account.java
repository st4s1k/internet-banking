package com.endava.entities;

import java.util.Objects;

public class Account {

    private Long id;
    private Double funds;

    public Account(Long id, Double funds) {
        this.id = id;
        this.funds = funds;
    }

    public Long getId() {
        return id;
    }

    public Double getFunds() {
        return funds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(funds, account.funds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, funds);
    }
}
