package com.jordanrobin.financial_erp.domain.member;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import com.jordanrobin.financial_erp.domain.auth.role.Role;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@Entity
@Table(
    name = "member_roles",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_member_roles_member_role",
        columnNames = {"member_id", "role_id"}
    )
)
public class MemberRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}
