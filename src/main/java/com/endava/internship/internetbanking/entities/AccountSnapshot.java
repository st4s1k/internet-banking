package com.endava.internship.internetbanking.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@Entity
@Builder
@Table(name = "t_account_history")
public class AccountSnapshot {

    @EqualsAndHashCode.Exclude
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NonNull
    @Builder.Default
    @NotNull
    @Column(name = "date_time")
    private LocalDateTime dateTime = LocalDateTime.now();

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @NonNull
    @NotNull
    @Column(name = "funds")
    private BigDecimal funds;
}
