package com.endava;

public class Account {

    private Long id;
    private Long funds;

    public Account(Long id, Long funds) {
        this.id = id;
        this.funds = funds;
    }

    public Long getId() {
        return id;
    }

    public Long getFunds() {
        return funds;
    }
}
