package com.endava;

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
}
