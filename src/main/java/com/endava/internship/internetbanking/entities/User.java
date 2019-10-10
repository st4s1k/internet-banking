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

    public static User from(@NonNull UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .build();
    }

    public UserDTO dto() {
        return new UserDTO(id, name);
    }
}
