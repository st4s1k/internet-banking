package com.endava.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

public class UserDTO {

    private Long id;

    @NotNull(message = "Name cannot be null")
    @Size(min = 3, max = 20,
            message = "Name must be between 3 and 20 characters")
    private String name;

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
        return o instanceof UserDTO
                && (this == o || Objects.equals(name, ((UserDTO) o).name));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
