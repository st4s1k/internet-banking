package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.AccountDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "funds")
    private BigDecimal funds = BigDecimal.ZERO;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public AccountDTO dto() {
        return new AccountDTO(id, funds, user.getId());
    }
}
