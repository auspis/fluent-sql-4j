package integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for PreparedStatementRenderer with H2 in-memory database.
 */
@IntegrationTest
class PreparedStatementRendererTest {

    private Connection connection;
    private PreparedStatementRenderer specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        specFactory = new PreparedStatementRenderer();

        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
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
                .where(Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John Doe"))))
                .build();

        // Generate SQL and parameters using PreparedStatementRenderer
        AstContext context = new AstContext();
        PreparedStatementSpec result = selectStmt.accept(specFactory, context);

        // Verify generated SQL and parameters
        assertThat(result.sql()).isEqualTo("SELECT * FROM \"users\" WHERE \"name\" = ?");
        assertThat(result.parameters()).containsExactly("John Doe");

        // Execute the generated SQL on real H2 database to verify it works
        try (var ps = connection.prepareStatement(result.sql())) {
            for (int i = 0; i < result.parameters().size(); i++) {
                ps.setObject(i + 1, result.parameters().get(i));
            }

            var rs = ps.executeQuery();
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getInt("age")).isEqualTo(30);
            assertThat(rs.next()).isFalse();
        }
    }
}
