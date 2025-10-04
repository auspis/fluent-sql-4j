package lan.tlab.r4j.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreparedStatementVisitorTest {

    private Connection connection;
    private PreparedStatementVisitor visitor;

    @BeforeEach
    void setUp() throws SQLException {
        // H2 in-memory database with standard SQL mode
        String jdbcUrl = "jdbc:h2:mem:testdb;MODE=REGULAR;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";
        connection = DriverManager.getConnection(jdbcUrl, "sa", "");
        visitor = new PreparedStatementVisitor();

        // Create a simple test table
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "CREATE TABLE users (id INTEGER PRIMARY KEY, first_name VARCHAR(50), last_name VARCHAR(50), age INTEGER)");
            stmt.execute("INSERT INTO users VALUES (1, 'John', 'Doe', 30)");
            stmt.execute("INSERT INTO users VALUES (2, 'Jane', 'Smith', 25)");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void selectUserByNameGeneratesCorrectSqlAndParameters() throws SQLException {
        // Build SELECT statement using the AST
        SelectStatement selectStmt = SelectStatement.builder()
                .select(Select.of()) // Empty select generates SELECT *
                .from(From.fromTable("users"))
                .where(Where.of(Comparison.eq(ColumnReference.of("users", "first_name"), Literal.of("John"))))
                .build();

        // Generate SQL and parameters using PreparedStatementVisitor
        AstContext context = new AstContext();
        PsDto result = selectStmt.accept(visitor, context);

        // Verify generated SQL and parameters
        assertThat(result.sql()).isEqualTo("SELECT * FROM \"users\" WHERE \"first_name\" = ?");
        assertThat(result.parameters()).containsExactly("John");

        // Execute the generated SQL on real H2 database to verify it works
        try (var ps = connection.prepareStatement(result.sql())) {
            for (int i = 0; i < result.parameters().size(); i++) {
                ps.setObject(i + 1, result.parameters().get(i));
            }

            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("first_name")).isEqualTo("John");
            assertThat(rs.getString("last_name")).isEqualTo("Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.next()).isFalse();
        }
    }
}
