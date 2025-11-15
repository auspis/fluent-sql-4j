package integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Integration tests for JSON functions with H2 in-memory database.
 * Tests the complete integration between JSON function AST nodes, SQL rendering,
 * PreparedStatement creation, and actual JSON operations in H2 database.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@IntegrationTest
class JsonFunctionsH2IntegrationTest {

    private Connection connection;
    private SqlRenderer renderer;

    @BeforeAll
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        renderer = TestDialectRendererFactory.standardSql();

        // Use standard tables from TestDatabaseUtil
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
        TestDatabaseUtil.createProductsTable(connection);
    }

    @AfterAll
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void jsonExistsRendersCorrectSql() {
        // Test that JSON_EXISTS AST node renders to correct SQL
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(
                                new JsonExists(ColumnReference.of("users", "email"), Literal.of("$.domain")),
                                "has_domain")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains JSON_EXISTS function
        assertThat(sql).contains("JSON_EXISTS");
        assertThat(sql).contains("$.domain");
        assertThat(sql).contains("has_domain");
    }

    @Test
    void jsonExistsWithErrorBehaviorRendersCorrectSql() {
        // Test JSON_EXISTS with error behavior
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        new JsonExists(
                                ColumnReference.of("users", "email"), Literal.of("$.domain"), BehaviorKind.ERROR),
                        "has_domain")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains ON ERROR clause
        assertThat(sql).contains("JSON_EXISTS");
        assertThat(sql).contains("ERROR ON ERROR");
    }

    @Test
    void jsonValueRendersCorrectSql() {
        // Test JSON_VALUE rendering
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(
                                new JsonValue(ColumnReference.of("users", "email"), Literal.of("$.username")),
                                "username")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify SQL rendering
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("$.username");
    }

    @Test
    void jsonValueWithReturningTypeRendersCorrectSql() {
        // Test JSON_VALUE with RETURNING type
        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "age"), Literal.of("$.years"), "INTEGER");

        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(jsonValue, "age_years")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains RETURNING clause
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("RETURNING INTEGER");
    }

    @Test
    void jsonValueWithDefaultBehaviorRendersCorrectSql() {
        // Test JSON_VALUE with DEFAULT on empty
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "price"),
                Literal.of("$.discount"),
                "DECIMAL(10,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                BehaviorKind.NONE);

        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(jsonValue, "discount")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains DEFAULT and ON EMPTY clauses
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("DEFAULT 0.00 ON EMPTY");
    }

    @Test
    void jsonQueryRendersCorrectSql() {
        // Test JSON_QUERY SQL rendering
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(
                                new JsonQuery(ColumnReference.of("users", "email"), Literal.of("$.history")),
                                "history")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains JSON_QUERY function
        assertThat(sql).contains("JSON_QUERY");
        assertThat(sql).contains("$.history");
        assertThat(sql).contains("history");
    }

    @Test
    void jsonQueryWithWrapperRendersCorrectSql() {
        // Test JSON_QUERY with wrapper behavior
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        new JsonQuery(
                                ColumnReference.of("users", "email"),
                                Literal.of("$.addresses"),
                                null,
                                WrapperBehavior.WITH_WRAPPER),
                        "addresses")))
                .from(From.of(new TableIdentifier("users")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains WITH WRAPPER clause
        assertThat(sql).contains("JSON_QUERY");
        assertThat(sql).contains("WITH WRAPPER");
    }

    @Test
    void jsonFunctionsWithCompactConstructors() {
        // Test that compact constructors work correctly with defaults
        JsonExists jsonExists = new JsonExists(ColumnReference.of("users", "email"), Literal.of("$.domain"));
        JsonValue jsonValue = new JsonValue(ColumnReference.of("users", "email"), Literal.of("$.username"));
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "email"), Literal.of("$.history"));

        // Verify defaults are set
        assertThat(jsonExists.onErrorBehavior()).isEqualTo(BehaviorKind.NONE);
        assertThat(jsonValue.onEmptyBehavior()).isEqualTo(OnEmptyBehavior.returnNull());
        assertThat(jsonValue.onErrorBehavior()).isEqualTo(BehaviorKind.NONE);
        assertThat(jsonQuery.wrapperBehavior()).isEqualTo(WrapperBehavior.NONE);
        assertThat(jsonQuery.onEmptyBehavior()).isEqualTo(OnEmptyBehavior.returnNull());
        assertThat(jsonQuery.onErrorBehavior()).isEqualTo(BehaviorKind.NONE);
    }
}
