package com.endava.internship.internetbanking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor

@JsonIgnoreType

@Entity
@Table(name = "t_account_history")
public class AccountSnapshot {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull
    @Column(name = "date_time")
    @NonNull
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    @NonNull
    private Account account;

    @NotNull
    @Column(name = "funds")
    @NonNull
    private BigDecimal funds;
}
