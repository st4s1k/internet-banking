package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@JsonIgnoreType

@Entity
@Table(name = "t_user")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "name")
    @NonNull
    private String name;

    @Transient
    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private Collection<Account> accounts;

    public UserDTO dto() {
        return new UserDTO(id, name);
    }
}
