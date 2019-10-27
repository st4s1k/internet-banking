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
@Table(name = "t_transfer")
public class Transfer {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;

    @NotNull
    @Column(name = "date_time")
    @NonNull
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();

    @NotNull
    @Column(name = "funds")
    @NonNull
    private BigDecimal funds;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "source_account_id")
    @NonNull
    private Account sourceAccount;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "destination_account_id")
    @NonNull
    private Account destinationAccount;
}
