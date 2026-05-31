package com.jordanrobin.financial_erp.domain.auth.privilege;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@ToString(of = {"name"})
@Builder
@Entity
@Table(name = "privileges")
public class Privilege {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UuidCreator.getTimeOrderedEpoch();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private PrivilegeName name;

    private String description;
}
