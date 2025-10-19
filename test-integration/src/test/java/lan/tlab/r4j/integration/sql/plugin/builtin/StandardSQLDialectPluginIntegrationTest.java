package lan.tlab.r4j.integration.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.sql.plugin.RegistryResult;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectRegistry;
import lan.tlab.r4j.sql.plugin.builtin.StandardSQLDialectPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for StandardSQLDialectPlugin using a real database.
 * <p>
 * These tests verify that the plugin works correctly with the DSL and can
 * successfully execute queries against a real database (H2 in standard SQL mode).
 */
class StandardSQLDialectPluginIntegrationTest {

    private Connection connection;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.createProductsTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);

        // Get renderer from the plugin
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
        renderer = plugin.createRenderer();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void shouldCreateValidRenderer() {
        assertThat(renderer).isNotNull();
    }

    @Test
    void shouldExecuteSimpleQueryUsingDSL() throws SQLException {
        List<List<Object>> rows = ResultSetUtil.list(
                DSL.select("name", "email").from("users").buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getString("email")));

        assertThat(rows)
                .hasSize(10)
                .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                .contains(tuple("John Doe", "john@example.com"), tuple("Jane Smith", "jane@example.com"));
    }

    @Test
    void shouldExecuteWhereQueryUsingDSL() throws SQLException {
        List<List<Object>> rows = ResultSetUtil.list(
                DSL.select("name", "age")
                        .from("users")
                        .where("name")
                        .eq("John Doe")
                        .buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getInt("age")));

        assertThat(rows)
                .hasSize(1)
                .extracting(r -> (String) r.get(0), r -> (Integer) r.get(1))
                .containsExactly(tuple("John Doe", 30));
    }

    @Test
    void shouldExecuteOrderByQueryUsingDSL() throws SQLException {
        List<String> names = ResultSetUtil.list(
                DSL.select("name").from("users").orderBy("name").buildPreparedStatement(connection),
                r -> r.getString("name"));

        assertThat(names).hasSize(10).startsWith("Alice", "Bob", "Charlie");
        // Verify we got all 10 users sorted alphabetically
        assertThat(names)
                .containsExactly(
                        "Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry", "Jane Smith", "John Doe");
    }

    @Test
    void shouldBeDiscoverableViaRegistry() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        assertThat(registry.isSupported("StandardSQL")).isTrue();

        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL", "2008");
        assertThat(result).isInstanceOf(RegistryResult.Success.class);

        SqlRenderer registryRenderer = result.orElseThrow();
        assertThat(registryRenderer).isNotNull();
    }

    @Test
    void shouldUseStandardSQLSyntaxForPagination() {
        String sql = DSL.select("name")
                .from("users")
                .orderBy("name")
                .offset(5)
                .fetch(3)
                .build();

        // Standard SQL:2008 uses OFFSET...ROWS FETCH NEXT...ROWS ONLY
        assertThat(sql).contains("OFFSET 5 ROWS");
        assertThat(sql).contains("FETCH NEXT 3 ROWS ONLY");
    }

    @Test
    void shouldHandleComplexQueries() throws SQLException {
        List<List<Object>> rows = ResultSetUtil.list(
                DSL.select("name", "age")
                        .from("users")
                        .where("age")
                        .gt(25)
                        .orderByDesc("age")
                        .offset(0)
                        .fetch(5)
                        .buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getInt("age")));

        assertThat(rows).isNotEmpty().hasSizeLessThanOrEqualTo(5);

        // Verify all ages are > 25
        for (List<Object> row : rows) {
            assertThat((Integer) row.get(1)).isGreaterThan(25);
        }
    }
}
