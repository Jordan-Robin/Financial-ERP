package com.jordanrobin.financial_erp.domain.auth.role;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import com.jordanrobin.financial_erp.domain.auth.privilege.Privilege;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = {"name"}, callSuper = false)
@ToString(of = {"name"})
@Builder
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 50)
    private RoleName name;

    private String description;

    @Column(nullable = false)
    private boolean isSystem;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_privileges",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    @Builder.Default
    private Set<Privilege> privileges = new HashSet<>();
}
