package pe.com.imperioperu.catalog.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.DriverManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
class PostgresMigrationIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18.4-alpine")
        .withDatabaseName("imperio_test").withUsername("imperio").withPassword("test-only");

    @Test
    void migrationsCreateTheApprovedAdministrativeTaxonomy() throws Exception {
        var result = Flyway.configure().dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()).load().migrate();
        assertThat(result.migrationsExecuted).isEqualTo(2);
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement()) {
            try (var rows = statement.executeQuery("select root_type, count(*) from category where parent_id is not null group by root_type order by root_type")) {
                assertThat(rows.next()).isTrue();
                assertThat(rows.getString(1)).isEqualTo("DENTISTRY");
                assertThat(rows.getInt(2)).isEqualTo(15);
                assertThat(rows.next()).isTrue();
                assertThat(rows.getString(1)).isEqualTo("MEDICINE");
                assertThat(rows.getInt(2)).isEqualTo(32);
            }
        }
    }
}
