package com.jordanrobin.financial_erp.domain.auth.privilege;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@ToString(of = {"name"})
@Builder
@Entity
@Table(name = "privileges")
public class Privilege extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private PrivilegeName name;

    private String description;
}
