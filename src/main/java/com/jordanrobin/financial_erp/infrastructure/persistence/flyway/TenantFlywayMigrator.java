package com.jordanrobin.financial_erp.infrastructure.persistence.flyway;

import com.jordanrobin.financial_erp.shared.exception.resource.ResourceAlreadyExistsException;
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
        if (verifyIfSchemaAlreadyExists(schemaName)) {
            throw new ResourceAlreadyExistsException("Base de donnée", "schema", schemaName);
        }

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

    private boolean verifyIfSchemaAlreadyExists(String schemaName) {
        String sql = "SELECT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = ?)";
        Boolean schemaExists = jdbcTemplate.queryForObject(sql, Boolean.class, schemaName);

        return Boolean.TRUE.equals(schemaExists);
    }

    private void createSchema(String schemaName) {
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);
    }
}
