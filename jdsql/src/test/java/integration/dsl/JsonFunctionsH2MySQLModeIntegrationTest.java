package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for JSON functions using H2 database in MySQL compatibility mode.
 *
 * <p><b>NOTE:</b> These tests are currently disabled because H2 does not support the MySQL/SQL standard
 * JSON functions (JSON_VALUE, JSON_EXISTS) even in MySQL compatibility mode. These tests verify
 * that our DSL generates correct SQL syntax, but require a real MySQL or PostgreSQL database to execute.
 *
 * <p>These tests are kept as documentation of the expected behavior and can be enabled when
 * running against actual MySQL/PostgreSQL databases in integration environments.
 */
@IntegrationTest
class JsonFunctionsH2MySQLModeIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2JsonConnection();

        // Use MySQL dialect DSL since we're testing MySQL-compatible JSON functions
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        dsl = registry.dslFor("mysql", "8.0.0").orElseThrow();

        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    @Disabled("H2 does not support JSON_VALUE function - requires actual MySQL/PostgreSQL database")
    void selectWithJsonValueInWhere() throws SQLException {
        // Frank (id=8) has address in Milan prepopulated
        // Select users from Milan using JSON_VALUE
        try (PreparedStatement ps = dsl.select("id", "name")
                        .from("users")
                        .where()
                        .jsonValue("address", "$.city")
                        .eq("Milan")
                        .buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery()) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(8);
            assertThat(rs.getString("name")).isEqualTo("Frank");
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    @Disabled("H2 does not support JSON_EXISTS function - requires actual MySQL/PostgreSQL database")
    void selectWithJsonExistsInWhere() throws SQLException {
        // Frank (id=8) and Grace (id=9) have email in their preferences prepopulated
        // Henry (id=10) does not have email in preferences
        // Select users who have email in preferences using JSON_EXISTS
        try (PreparedStatement ps = dsl.select("id", "name")
                        .from("users")
                        .where()
                        .jsonExists("preferences", "$[?(@ == \"email\")]")
                        .exists()
                        .buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery()) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(8);
            assertThat(rs.getString("name")).isEqualTo("Frank");

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(9);
            assertThat(rs.getString("name")).isEqualTo("Grace");

            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    @Disabled("H2 does not support JSON_VALUE/JSON_EXISTS functions - requires actual MySQL/PostgreSQL database")
    void selectWithMixedJsonFunctionsInWhere() throws SQLException {
        // Henry (id=10) is from Turin and has push in preferences prepopulated
        // Select users from Turin who have push notifications using mixed JSON functions
        try (PreparedStatement ps = dsl.select("id", "name")
                        .from("users")
                        .where()
                        .jsonValue("address", "$.city")
                        .eq("Turin")
                        .and()
                        .jsonExists("preferences", "$[?(@ == \"push\")]")
                        .exists()
                        .buildPreparedStatement(connection);
                ResultSet rs = ps.executeQuery()) {

            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(10);
            assertThat(rs.getString("name")).isEqualTo("Henry");
            assertThat(rs.next()).isFalse();
        }
    }
}
