package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.IntegrationTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for GroupByBuilder with H2 in-memory database.
 * Tests GROUP BY clause with explicit alias support for multi-table scenarios.
 */
@IntegrationTest
class GroupByBuilderIntegrationTest {

    private Connection connection;
    private DSL dsl;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.H2.createConnection();
        dsl = StandardSqlUtil.dsl();
        TestDatabaseUtil.H2.createCustomersTable(connection);
        TestDatabaseUtil.H2.insertSampleCustomers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.H2.closeConnection(connection);
    }

    @Test
    void groupByWithSingleColumn() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("country")
                .count("id")
                .as("customer_count")
                .from("customers")
                .groupBy()
                .column("country")
                .fetch(100)
                .build(connection);

        ResultSet rs = ps.executeQuery();
        int rowCount = 0;
        while (rs.next()) {
            rowCount++;
            assertThat(rs.getString("country")).isIn("USA", "UK");
            assertThat(rs.getInt("customer_count")).isGreaterThan(0);
        }
        assertThat(rowCount).isEqualTo(2); // 2 countries
        rs.close();
        ps.close();
    }

    @Test
    void groupByWithExplicitAlias() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("c", "country")
                .count("id")
                .as("customer_count")
                .from("customers")
                .as("c")
                .groupBy()
                .column("c", "country")
                .fetch(100)
                .build(connection);

        ResultSet rs = ps.executeQuery();
        assertThat(rs.next()).isTrue();
        assertThat(rs.next()).isTrue();
        assertThat(rs.next()).isFalse(); // 2 countries
        rs.close();
        ps.close();
    }

    @Test
    void groupByWithOrderBy() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("country")
                .count("id")
                .as("customer_count")
                .from("customers")
                .groupBy()
                .column("country")
                .orderBy()
                .asc("country")
                .build(connection);

        ResultSet rs = ps.executeQuery();
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("country")).isEqualTo("UK");
        assertThat(rs.next()).isTrue();
        assertThat(rs.getString("country")).isEqualTo("USA");
        assertThat(rs.next()).isFalse();
        rs.close();
        ps.close();
    }

    @Test
    void rejectsDotNotationInColumn() {
        assertThatThrownBy(() -> dsl.select()
                        .column("country")
                        .from("customers")
                        .groupBy()
                        .column("customers.country")
                        .fetch(1)
                        .build(connection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot notation: 'customers.country'");
    }

    @Test
    void rejectsDotNotationInAlias() {
        assertThatThrownBy(() -> dsl.select()
                        .column("country")
                        .from("customers")
                        .as("c")
                        .groupBy()
                        .column("c.invalid", "country")
                        .fetch(1)
                        .build(connection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference must not contain dot: 'c.invalid'");
    }
}
