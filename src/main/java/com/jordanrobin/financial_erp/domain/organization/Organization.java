package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import com.jordanrobin.financial_erp.domain.tenant.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.time.MonthDay;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@ToString(of = {"name"})
@Entity
@Table(name = "organizations")
public class Organization extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false, unique = true)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private LegalStatus legalStatus;

    @Column(length = 9)
    private String siren;

    @Column(length = 10)
    private String nafCode;

    @Column
    private MonthDay fiscalYearEndDate;

}
