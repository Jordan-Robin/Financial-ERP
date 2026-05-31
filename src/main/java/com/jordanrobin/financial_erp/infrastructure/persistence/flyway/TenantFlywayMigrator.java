package com.jordanrobin.financial_erp.infrastructure.persistence.flyway;

import com.jordanrobin.financial_erp.shared.exception.auth.InvalidSchemaNameException;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
public class TenantFlywayMigrator {

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public void migrate(String schemaName) {
        createSchema(schemaName);

        Flyway.configure()
            .dataSource(dataSource)
            .schemas(schemaName)
            .defaultSchema(schemaName)
            .createSchemas(false)
            .locations("classpath:db/migration/tenant")
            .table("flyway_schema_history")
            .validateOnMigrate(true)
            .outOfOrder(false)
            .load()
            .migrate();
    }

    private void createSchema(String schemaName) {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
    }
}
