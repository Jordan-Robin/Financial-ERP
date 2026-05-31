package com.jordanrobin.financial_erp.domain.tenant;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@ToString(of = {"tenantSlug"})
@Builder
@Entity
@Table(name = "tenants", schema = "public")
public class Tenant extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(unique = true, nullable = false)
    private String schemaName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TenantStatus status;
}
