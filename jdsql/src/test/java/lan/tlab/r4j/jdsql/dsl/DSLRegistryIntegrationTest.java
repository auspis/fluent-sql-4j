package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests demonstrating DSLRegistry usage with different SQL dialects.
 * <p>
 * These tests show how users can leverage DSLRegistry to create dialect-specific
 * DSL instances and generate SQL for different database systems.
 */
@IntegrationTest
class DSLRegistryIntegrationTest {

    private DSLRegistry registry;

    @BeforeEach
    void setUp() {
        registry = DSLRegistry.createWithServiceLoader();
    }

    @Test
    void selectQuery_withMySQL_shouldUseBackticks() {
        // Get a MySQL-configured DSL instance
        Result<DSL> result = registry.dslFor("mysql", "8.0.35");
        DSL dsl = result.orElseThrow();

        // Build a SELECT query
        String sql = dsl.select("id", "name", "email")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .build();

        // MySQL uses backticks for identifiers
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`id`");
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");
        assertThat(sql).contains("WHERE `users`.`status` = 'active'");
    }

    @Test
    void selectQuery_withStandardSQL_shouldUseDoubleQuotes() {
        // Get a Standard SQL:2008 configured DSL instance
        Result<DSL> result = registry.dslFor("standardsql", "2008");
        DSL dsl = result.orElseThrow();

        // Build a SELECT query
        String sql = dsl.select("id", "name", "email")
                .from("users")
                .where()
                .column("status")
                .eq("active")
                .build();

        // Standard SQL uses double quotes for identifiers
        assertThat(sql).contains("\"users\"");
        assertThat(sql).contains("\"id\"");
        assertThat(sql).contains("\"name\"");
        assertThat(sql).contains("\"email\"");
        assertThat(sql).contains("WHERE \"users\".\"status\" = 'active'");
    }

    @Test
    void insertQuery_withMySQL_shouldGenerateCorrectSQL() {
        DSL dsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        String sql = dsl.insertInto("users")
                .set("name", "John Doe")
                .set("email", "john@example.com")
                .set("age", 30)
                .build();

        assertThat(sql).contains("INSERT INTO `users`");
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");
        assertThat(sql).contains("`age`");
    }

    @Test
    void updateQuery_withStandardSQL_shouldGenerateCorrectSQL() {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        String sql = dsl.update("users")
                .set("status", "inactive")
                .where()
                .column("last_login")
                .isNull()
                .build();

        assertThat(sql).contains("UPDATE \"users\"");
        assertThat(sql).contains("SET \"status\" = 'inactive'");
        assertThat(sql).contains("WHERE \"users\".\"last_login\" IS NULL");
    }

    @Test
    void deleteQuery_withMySQL_shouldGenerateCorrectSQL() {
        DSL dsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        String sql = dsl.deleteFrom("users")
                .where()
                .column("created_at")
                .lt("2020-01-01")
                .build();

        assertThat(sql).contains("DELETE FROM `users`");
        assertThat(sql).contains("WHERE `users`.`created_at` < '2020-01-01'");
    }

    @Test
    void complexQuery_withJoinsAndAggregations_shouldWork() {
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION)
                .orElseThrow();

        String sql = dsl.select("o.order_id", "c.name")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .where()
                .column("o.status")
                .eq("completed")
                .build();

        assertThat(sql).contains("SELECT");
        assertThat(sql).contains("INNER JOIN");
        assertThat(sql).contains("WHERE");
    }

    @Test
    void dslFor_withoutVersion_shouldUseDefaultVersion() {
        Result<DSL> result = registry.dslFor("mysql");

        assertThat(result.isSuccess()).isTrue();
        DSL dsl = result.orElseThrow();

        String sql = dsl.select("*").from("test").build();
        // Should still use MySQL dialect (backticks)
        assertThat(sql).contains("`test`");
    }

    @Test
    void dslFor_withInvalidDialect_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor("invalid-dialect", "1.0.0");

        assertThat(result.isFailure()).isTrue();
        assertThat(((Result.Failure<DSL>) result).message()).contains("No plugin found");
    }

    @Test
    void multipleDialects_inSameTest_shouldWorkCorrectly() {
        // Get DSL instances for different dialects
        DSL mysqlDsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
        DSL standardSqlDsl = registry.dslFor("standardsql", "2008").orElseThrow();

        // Generate SQL with different dialects
        String mysqlSql = mysqlDsl.select("name").from("users").build();
        String standardSql = standardSqlDsl.select("name").from("users").build();

        // Verify they use different identifier quoting
        assertThat(mysqlSql).contains("`users`").contains("`name`");
        assertThat(standardSql).contains("\"users\"").contains("\"name\"");
    }
}
