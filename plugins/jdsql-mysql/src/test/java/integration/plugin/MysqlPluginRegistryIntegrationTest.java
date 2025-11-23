package integration.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for MySQLDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader.
 */
@IntegrationTest
class MysqlPluginRegistryIntegrationTest {

    private SqlDialectPluginRegistry pluginRegistry;

    @BeforeEach
    void setUp() throws SQLException {
        pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader();
    }

    //    private void createMySQLUsersTable(Connection connection) throws SQLException {
    //        try (var stmt = connection.createStatement()) {
    //            stmt.execute("DROP TABLE IF EXISTS users");
    //            stmt.execute(
    //                    """
    //                    CREATE TABLE users (
    //                    id INTEGER PRIMARY KEY,
    //                    name VARCHAR(50),
    //                    email VARCHAR(100),
    //                    age INTEGER,
    //                    active BOOLEAN,
    //                    birthdate DATE,
    //                    createdAt TIMESTAMP)
    //                    """);
    //        }
    //    }
    //
    //    private void insertMySQLSampleUsers(Connection connection) throws SQLException {
    //        try (var stmt = connection.createStatement()) {
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30, true, '1990-01-01',
    // '2023-01-01')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25, true, '1995-01-01',
    // '2023-01-01')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (3, 'Bob', 'bob@example.com', 15, false, '2005-01-01',
    // '2023-01-01')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (4, 'Alice', 'alice@example.com', 35, true, '1990-01-01',
    // '2023-01-01')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (5, 'Charlie', 'charlie@example.com', 30, true, '1991-01-01',
    // '2023-01-02')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (6, 'Diana', 'diana@example.com', 25, false, '1996-01-01',
    // '2023-01-03')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (7, 'Eve', 'eve@example.com', 40, true, '1985-01-01',
    // '2023-01-04')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (8, 'Frank', 'frank@example.com', 35, true, '1990-02-01',
    // '2023-01-05')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (9, 'Grace', 'grace@example.com', 28, false, '1997-01-01',
    // '2023-01-06')");
    //            stmt.execute(
    //                    "INSERT INTO users VALUES (10, 'Henry', 'henry@example.com', 30, true, '1995-01-01',
    // '2023-01-07')");
    //        }
    //    }
    //
    //    @AfterEach
    //    void tearDown() throws SQLException {
    //        if (connection != null && !connection.isClosed()) {
    //            connection.close();
    //        }
    //    }

    @Test
    void registration() {
        assertThat(pluginRegistry.isEmpty()).isFalse();
        assertThat(pluginRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isTrue();
        assertThat(pluginRegistry.isSupported("mysql")).isTrue(); // case-insensitive
        assertThat(pluginRegistry.isSupported("MYSQL")).isTrue();
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        assertThat(pluginRegistry.size()).isGreaterThanOrEqualTo(2);

        assertThat(pluginRegistry.getSupportedDialects()).contains("mysql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isFalse();

        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        assertThat(newRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isTrue();
        Result<DialectRenderer> result = newRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void getRenderer() {
        Result<DialectRenderer> result = pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");

        assertThat(result).isInstanceOf(Result.Success.class);
        DialectRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match MySQL 8.x versions (using ^8.0.0 range)
        Result<DialectRenderer> version800 =
                pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.0");
        assertThat(version800).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version8035 =
                pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");
        assertThat(version8035).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version810 =
                pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.1.0");
        assertThat(version810).isInstanceOf(Result.Success.class);

        // Should NOT match MySQL 5.7 or 9.0
        Result<DialectRenderer> version57 =
                pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "5.7.42");
        assertThat(version57).isInstanceOf(Result.Failure.class);

        Result<DialectRenderer> version90 = pluginRegistry.getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "9.0.0");
        assertThat(version90).isInstanceOf(Result.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<DialectRenderer> result = pluginRegistry.getRenderer(MysqlDialectPlugin.DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldGenerateMySQLSyntaxWithBackticks() throws SQLException {
        DialectRenderer renderer = pluginRegistry
                .getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35")
                .orElseThrow();

        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .where(Where.of(Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25))))
                .build();

        String sql = renderer.renderSql(statement);
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");
        assertThat(sql)
                .isEqualTo(
                        """
            SELECT `users`.`name`, `users`.`email` \
            FROM `users` \
            WHERE `users`.`age` > 25\
            """);
    }

    @Test
    void shouldGenerateMySQLPaginationSyntax() {
        DialectRenderer renderer = pluginRegistry
                .getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35")
                .orElseThrow();

        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(new Fetch(5, 3))
                .build();

        String sql = renderer.renderSql(statement);
        assertThat(sql).contains("LIMIT 3 OFFSET 5");
        assertThat(sql).doesNotContain("OFFSET 5 ROWS");
        assertThat(sql).doesNotContain("FETCH NEXT");
    }

    @Test
    void groupConcatFunction() throws SQLException {
        DialectRenderer renderer = pluginRegistry
                .getDialectRenderer(MysqlDialectPlugin.DIALECT_NAME, "8.0.35")
                .orElseThrow();

        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "age")),
                        new ScalarExpressionProjection(
                                new CustomFunctionCall(
                                        "GROUP_CONCAT",
                                        List.of(ColumnReference.of("", "name")),
                                        Map.of("SEPARATOR", ", ")),
                                "names")))
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "age"))))
                .groupBy(GroupBy.of(ColumnReference.of("users", "age")))
                .fetch(new Fetch(5, 3))
                .build();
        String sql = renderer.renderSql(statement);

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT `users`.`age`, \
            GROUP_CONCAT(`name` SEPARATOR ', ') AS names \
            FROM `users` \
            GROUP BY `users`.`age` \
            ORDER BY `users`.`age` ASC \
            LIMIT 3 OFFSET 5\
            """);
    }
}
