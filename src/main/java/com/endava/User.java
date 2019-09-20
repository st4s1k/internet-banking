package com.endava;

public class User {

    private Long id;
    private String name;
    private Account account;

    public Account getAccount() {
        return account;
    }

    public Long getId() {
        return id;
    }
}
