package com.endava.internship.internetbanking.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String name;

    public User() {
        id = null;
        name = null;
    }

    private User(Long id, String name) {
        this.id = id;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof User
                && (this == o || Objects.equals(name, ((User) o).name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public User clone() {
        return new Builder().setId(id).setName(name).build();
    }

    public static class Builder {

        private Long id;

        private String name;

        private Builder() {
        }

        public Builder setId(@NotNull Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(@NotNull @Size(min = 3, max = 20) String name) {
            this.name = name;
            return this;
        }

        public User build() {
            return new User(this.id, this.name);
        }

    }
}
