package com.jordanrobin.financial_erp.domain.member;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import com.jordanrobin.financial_erp.domain.auth.user.User;
import com.jordanrobin.financial_erp.domain.organization.Organization;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@ToString(of = {"firstName", "lastName"})
@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;
}
