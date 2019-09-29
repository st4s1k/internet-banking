package com.endava.entities;

import com.endava.annotations.Column;
import com.endava.dto.UserDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class User implements Entity, Cloneable {

    public static class Builder {

        private Long id;

        private String name;

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

    public static String TABLE_NAME = "users";
    public static String ID_NAME = "id";

    @Column("id")
    private final Long id;
    @Column("name")
    private final String name;

    public User() {
        id = null;
        name = null;
    }

    private User(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static User from(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
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
}
