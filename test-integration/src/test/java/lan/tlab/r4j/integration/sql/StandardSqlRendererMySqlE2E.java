package lan.tlab.r4j.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.ddl.CreateTableStatement;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition.ColumnDefinitionBuilder;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StandardSqlRendererMySqlE2E {
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
        renderer = SqlRendererFactory.mysql();
        // Create table using CreateTableStatement and renderer
        CreateTableStatement createTable = new CreateTableStatement(TableDefinition.builder()
                .table(new TableIdentifier("Customer"))
                .columns(List.of(
                        ColumnDefinitionBuilder.integer("id").build(),
                        ColumnDefinitionBuilder.varchar("name").build(),
                        ColumnDefinitionBuilder.integer("score").build(),
                        ColumnDefinition.builder("createdAt", DataType.timestamp())
                                .build()))
                .primaryKey(new PrimaryKey("id"))
                .build());
        String createTableSql = createTable.accept(renderer, new AstContext());
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSql);

            InsertStatement insertAlice = InsertStatement.builder()
                    .table(new TableIdentifier("Customer"))
                    .data(lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues.of(
                            Literal.of(1), Literal.of("Alice"), Literal.of(400), Literal.of("2025-08-31 23:23:23")))
                    .build();
            InsertStatement insertBob = InsertStatement.builder()
                    .table(new TableIdentifier("Customer"))
                    .data(lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues.of(
                            Literal.of(2), Literal.of("Bob"), Literal.of(500), Literal.of("2025-08-31 23:23:24")))
                    .build();
            stmt.execute(insertAlice.accept(renderer, new AstContext()));
            stmt.execute(insertBob.accept(renderer, new AstContext()));
        }
    }

    @AfterAll
    void tearDown() throws Exception {
        connection.close();
        mysql.stop();
    }

    @Test
    void selectFromCustomer() throws Exception {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name"))))
                .from(From.fromTable("Customer"))
                .where(Where.of(Comparison.ne(ColumnReference.of("Customer", "name"), Literal.of("unknown"))))
                .orderBy(OrderBy.of(
                        Sorting.desc(ColumnReference.of("Customer", "name")),
                        Sorting.asc(ColumnReference.of("Customer", "createdAt"))))
                .fetch(Fetch.builder().rows(5).offset(0).build())
                .build();
        String sql = statement.accept(renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT `Customer`.`name` \
                        FROM `Customer` \
                        WHERE `Customer`.`name` != 'unknown' \
                        ORDER BY `Customer`.`name` DESC, `Customer`.`createdAt` ASC \
                        LIMIT 5 OFFSET 0\
                        """);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Bob");
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Alice");
            assertThat(rs.next()).isFalse();
        }
    }

    @Test
    void whereLiteral() throws Exception {
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("Customer", "name"))))
                .from(From.fromTable("Customer"))
                .where(Where.of(Comparison.ne(ColumnReference.of("Customer", "name"), Literal.of("Alice"))))
                .build();
        String sql = statement.accept(renderer, new AstContext());
        System.out.println(sql);
        assertThat(sql)
                .isEqualTo(
                        """
        				SELECT `Customer`.`name` \
        				FROM `Customer` \
        				WHERE `Customer`.`name` != 'Alice'\
        				""");
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString(1)).isEqualTo("Bob");
            assertThat(rs.next()).isFalse();
        }
    }
}
