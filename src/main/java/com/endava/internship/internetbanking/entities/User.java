package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.UserDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotNull
    @Column(name = "name")
    private String name;

    public UserDTO dto() {
        return new UserDTO(id, name);
    }
}
