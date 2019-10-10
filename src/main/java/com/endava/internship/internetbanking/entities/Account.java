package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.AccountDTO;
import com.endava.internship.internetbanking.services.UserService;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Optional;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "funds")
    @Builder.Default
    private BigDecimal funds = BigDecimal.ZERO;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static Account from(@NonNull AccountDTO accountDTO,
                               @NonNull UserService userService) {
        Account.AccountBuilder accountBuilder = Account.builder()
                .id(accountDTO.getId())
                .funds(accountDTO.getFunds());
        Optional<User> optUser = userService.findById(accountDTO.getUserId());
        optUser.ifPresent(accountBuilder::user);
        return accountBuilder.build();
    }

    public AccountDTO dto() {
        return new AccountDTO(id, funds, user.getId());
    }
}
