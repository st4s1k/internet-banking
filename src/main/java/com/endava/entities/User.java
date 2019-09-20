package com.endava.entities;

public class User {

    public static class Builder {

        private Long id;
        private String name;
        private Account account;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setAccount(Account account) {
            this.account = account;
            return this;
        }

        public User build() {
            return new User(this.id, this.name, this.account);
        }
    }

    private Long id;
    private String name;
    private Account account;

    private User(Long id, String name, Account account) {
        this.id = id;
        this.name = name;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Account getAccount() {
        return account;
    }
}
