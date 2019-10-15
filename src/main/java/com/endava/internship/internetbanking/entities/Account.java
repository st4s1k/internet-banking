package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.AccountDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Builder
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "funds")
    @Builder.Default
    private BigDecimal funds = ZERO;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AccountDTO dto() {
        return new AccountDTO(id, funds, user == null ? null : user.getId());
    }
}
