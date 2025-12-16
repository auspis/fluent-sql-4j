package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import lan.tlab.r4j.jdsql.test.util.annotation.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Integration tests demonstrating DSLRegistry usage with different SQL dialects.
 * <p>
 * These tests show how users can leverage DSLRegistry to create dialect-specific
 * DSL instances and generate SQL for different database systems.
 */
@ComponentTest
class DSLRegistryComponentTest {

    private DSLRegistry registry;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        registry = DSLRegistry.createWithServiceLoader();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void selectQuery_withMySQL_shouldUseBackticks() throws SQLException {
        // Get a MySQL-configured DSL instance
        Result<DSL> result =
                registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION);
        DSL dsl = result.orElseThrow();

        // Build a SELECT query
        dsl.select("id", "name", "email")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .buildPreparedStatement(connection);

        // MySQL uses backticks for identifiers
        assertThat(sqlCaptor.getValue()).contains("\"users\"");
        assertThat(sqlCaptor.getValue()).contains("\"id\"");
        assertThat(sqlCaptor.getValue()).contains("\"name\"");
        assertThat(sqlCaptor.getValue()).contains("\"email\"");
        assertThat(sqlCaptor.getValue()).contains("WHERE \"status\" = ?");
        verify(ps).setObject(1, "active");
    }

    @Test
    void selectQuery_withStandardSQL_shouldUseDoubleQuotes() throws SQLException {
        // Get a Standard SQL:2008 configured DSL instance
        Result<DSL> result =
                registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION);
        DSL dsl = result.orElseThrow();

        // Build a SELECT query
        dsl.select("id", "name", "email")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .buildPreparedStatement(connection);

        // Standard SQL uses double quotes for identifiers
        assertThat(sqlCaptor.getValue()).contains("\"users\"");
        assertThat(sqlCaptor.getValue()).contains("\"id\"");
        assertThat(sqlCaptor.getValue()).contains("\"name\"");
        assertThat(sqlCaptor.getValue()).contains("\"email\"");
        assertThat(sqlCaptor.getValue()).contains("WHERE \"status\" = ?");
        verify(ps).setObject(1, "active");
    }

    @Test
    void insertQuery_withMySQL_shouldGenerateCorrectSQL() throws SQLException {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        dsl.insertInto("users")
                .set("name", "John Doe")
                .set("email", "john@example.com")
                .set("age", 30)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).contains("INSERT INTO \"users\"");
        assertThat(sqlCaptor.getValue()).contains("\"name\"");
        assertThat(sqlCaptor.getValue()).contains("\"email\"");
        assertThat(sqlCaptor.getValue()).contains("\"age\"");
        verify(ps).setObject(1, "John Doe");
        verify(ps).setObject(2, "john@example.com");
        verify(ps).setObject(3, 30);
    }

    @Test
    void updateQuery_withStandardSQL_shouldGenerateCorrectSQL() throws SQLException {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        dsl.update("users")
                .set("status", "inactive")
                .where()
                .column("last_login")
                .isNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).contains("UPDATE \"users\"");
        assertThat(sqlCaptor.getValue()).contains("SET \"status\" = ?");
        assertThat(sqlCaptor.getValue()).contains("WHERE \"last_login\" IS NULL");
        verify(ps).setObject(1, "inactive");
    }

    @Test
    void deleteQuery_withMySQL_shouldGenerateCorrectSQL() throws SQLException {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        dsl.deleteFrom("users").where().column("created_at").lt("2020-01-01").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).contains("DELETE FROM \"users\"");
        assertThat(sqlCaptor.getValue()).contains("WHERE \"created_at\" < ?");
        verify(ps).setObject(1, "2020-01-01");
    }

    @Test
    void complexQuery_withJoinsAndAggregations_shouldWork() throws SQLException {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        dsl.select("o.order_id", "c.name")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .where()
                .column("o.status")
                .eq("completed")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).contains("SELECT");
        assertThat(sqlCaptor.getValue()).contains("INNER JOIN");
        assertThat(sqlCaptor.getValue()).contains("WHERE");
        verify(ps).setObject(1, "completed");
    }

    @Test
    void dslFor_withoutVersion_shouldUseDefaultVersion() throws SQLException {
        Result<DSL> result = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME);

        assertThat(result.isSuccess()).isTrue();
        DSL dsl = result.orElseThrow();

        dsl.select("*").from("test").buildPreparedStatement(connection);
        // Should still use MySQL dialect (backticks)
        assertThat(sqlCaptor.getValue()).contains("\"test\"");
    }

    @Test
    void dslFor_withInvalidDialect_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor("invalid-dialect", "1.0.0");

        assertThat(result.isFailure()).isTrue();
        assertThat(((Result.Failure<DSL>) result).message()).contains("No plugin found");
    }
}
