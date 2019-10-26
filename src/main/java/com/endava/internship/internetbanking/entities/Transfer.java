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
@Table(name = "t_transfer")
public class Transfer {

    @EqualsAndHashCode.Exclude
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Builder.Default
    @NotNull
    @Column(name = "date_time")
    private LocalDateTime dateTime = LocalDateTime.now();

    @NonNull
    @NotNull
    @Column(name = "funds")
    private BigDecimal funds;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @NonNull
    @NotNull
    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;
}
