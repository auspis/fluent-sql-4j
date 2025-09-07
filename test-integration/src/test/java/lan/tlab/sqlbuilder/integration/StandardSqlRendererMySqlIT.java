package lan.tlab.sqlbuilder.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.clause.from.From;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.clause.selection.Select;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StandardSqlRendererMySqlIT {
    @Container
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
        // TODO: create table from Table object
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE Customer (id INT PRIMARY KEY, name VARCHAR(255), score INTEGER, createdAt TIMESTAMP)");
            // TODO: support this kind of insert in InsertStatement
            stmt.execute(
                    """
            	INSERT INTO Customer VALUES \
            	(1, 'Alice', 400, '2025-08-31 23:23:23'), \
            	(2, 'Bob', 500, '2025-08-31 23:23:24')
            	""");
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
                .pagination(Pagination.builder().perPage(5).build())
                .build();
        String sql = statement.accept(renderer);
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
        String sql = statement.accept(renderer);
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
