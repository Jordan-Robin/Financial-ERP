package com.jordanrobin.financial_erp.domain.organization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {

    Optional<Organization> findBySiren(String siren);

    boolean existsBySiren(String siren);
}
