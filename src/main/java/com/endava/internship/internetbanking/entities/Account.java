package com.endava.internship.internetbanking.entities;

import com.endava.internship.internetbanking.dto.AccountDTO;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;

import static java.math.BigDecimal.ZERO;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

@Entity
@Builder
@Table(name = "t_account")
public class Account {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Column(name = "funds")
    @Builder.Default
    private BigDecimal funds = ZERO;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Transient
    @OneToMany(mappedBy = "account", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private Collection<AccountSnapshot> snapshots;

    @Transient
    @OneToMany(mappedBy = "sourceAccount", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private Collection<Transfer> lossTransfers;

    @Transient
    @OneToMany(mappedBy = "destinationAccount", fetch = LAZY, cascade = ALL, orphanRemoval = true)
    private Collection<Transfer> gainTransfers;

    public AccountDTO dto() {
        return new AccountDTO(id, funds, user == null ? null : user.getId());
    }
}
