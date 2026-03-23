package com.jordanrobin.financial_erp.domain.organization;

import com.jordanrobin.financial_erp.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

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
