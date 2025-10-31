package lan.tlab.r4j.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.BehaviorKind;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonExists;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonQuery;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.json.WrapperBehavior;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for JSON functions with MySQL database.
 * Tests that JSON function AST nodes are properly rendered to SQL.
 * <p>
 * Note: MySQL 8.0 has limited SQL:2016 JSON function support.
 * JSON_VALUE is supported, but JSON_EXISTS and JSON_QUERY are not.
 * This test validates the SQL rendering of the AST nodes.
 */
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JsonFunctionsMySqlIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private Connection connection;
    private SqlRenderer renderer;

    @BeforeAll
    void setUp() throws Exception {
        mysql.start();
        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        renderer = TestDialectRendererFactory.mysql();

        // Create table with JSON column
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("products"))
                .columns(java.util.List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinition.builder("data", new DataType.SimpleDataType("JSON"))
                                .build()))
                .primaryKey(new PrimaryKeyDefinition("id"))
                .build());
        String createTableSql = createTable.accept(renderer, new AstContext());

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);

            // Insert test data with JSON
            stmt.execute(
                    """
                    INSERT INTO products (id, name, data) VALUES
                    (1, 'Laptop', '{"price": 999.99, "tags": ["electronics", "computers"], "specs": {"cpu": "i7", "ram": "16GB"}}'),
                    (2, 'Mouse', '{"price": 29.99, "tags": ["electronics", "accessories"]}'),
                    (3, 'Book', '{"price": 19.99, "tags": ["books"]}'),
                    (4, 'Desk', '{"tags": ["furniture"]}')
                    """);
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void jsonExistsRendersCorrectSql() {
        // Test that JSON_EXISTS AST node renders to correct SQL
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(
                                JsonExists.of(ColumnReference.of("products", "data"), Literal.of("$.price")),
                                "has_price")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains JSON_EXISTS function
        assertThat(sql).contains("JSON_EXISTS");
        assertThat(sql).contains("$.price");
        assertThat(sql).contains("has_price");
    }

    @Test
    void jsonExistsWithErrorBehaviorRendersCorrectSql() {
        // Test JSON_EXISTS with error behavior
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        JsonExists.of(
                                ColumnReference.of("products", "data"), Literal.of("$.price"), BehaviorKind.ERROR),
                        "has_price")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains ON ERROR clause
        assertThat(sql).contains("JSON_EXISTS");
        assertThat(sql).contains("ERROR ON ERROR");
    }

    @Test
    void jsonValueExtractsScalarValue() throws Exception {
        // Test JSON_VALUE extraction (MySQL 8.0+ supports this)
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(
                                JsonValue.of(ColumnReference.of("products", "data"), Literal.of("$.price")), "price")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify SQL rendering
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("$.price");

        // Execute the query
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("name");
                String price = rs.getString("price");

                if ("Laptop".equals(name)) {
                    assertThat(price).isEqualTo("999.99");
                } else if ("Mouse".equals(name)) {
                    assertThat(price).isEqualTo("29.99");
                } else if ("Book".equals(name)) {
                    assertThat(price).isEqualTo("19.99");
                } else if ("Desk".equals(name)) {
                    assertThat(price).isNull(); // No price field
                }
            }
        }
    }

    @Test
    void jsonValueWithReturningTypeRendersCorrectSql() {
        // Test JSON_VALUE with RETURNING type
        JsonValue jsonValue =
                JsonValue.of(ColumnReference.of("products", "data"), Literal.of("$.price"), "DECIMAL(10,2)");

        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(jsonValue, "price")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains RETURNING clause
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("RETURNING DECIMAL(10,2)");
    }

    @Test
    void jsonValueWithDefaultBehaviorRendersCorrectSql() {
        // Test JSON_VALUE with DEFAULT on empty
        JsonValue jsonValue = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.price"),
                "DECIMAL(10,2)",
                BehaviorKind.DEFAULT,
                "0.00",
                BehaviorKind.NULL);

        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(jsonValue, "price")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains DEFAULT and ON ERROR clauses
        assertThat(sql).contains("JSON_VALUE");
        assertThat(sql).contains("DEFAULT 0.00 ON EMPTY");
        assertThat(sql).contains("NULL ON ERROR");
    }

    @Test
    void jsonQueryRendersCorrectSql() {
        // Test JSON_QUERY SQL rendering
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("products", "name")),
                        new ScalarExpressionProjection(
                                JsonQuery.of(ColumnReference.of("products", "data"), Literal.of("$.tags")), "tags")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains JSON_QUERY function
        assertThat(sql).contains("JSON_QUERY");
        assertThat(sql).contains("$.tags");
        assertThat(sql).contains("tags");
    }

    @Test
    void jsonQueryWithWrapperRendersCorrectSql() {
        // Test JSON_QUERY with wrapper behavior
        SelectStatement query = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(
                        JsonQuery.of(
                                ColumnReference.of("products", "data"),
                                Literal.of("$.tags"),
                                WrapperBehavior.WITH_WRAPPER),
                        "tags")))
                .from(From.of(new TableIdentifier("products")))
                .build();

        String sql = query.accept(renderer, new AstContext());

        // Verify the SQL contains WITH WRAPPER clause
        assertThat(sql).contains("JSON_QUERY");
        assertThat(sql).contains("WITH WRAPPER");
    }
}
