package integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.Select;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.IntegrationTest;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for AstToPreparedStatementSpecVisitor with H2 in-memory database.
 */
@IntegrationTest
class AstToPreparedStatementSpecVisitorTest {

    private Connection connection;
    private AstToPreparedStatementSpecVisitor specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        specFactory = new AstToPreparedStatementSpecVisitor();

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

        // Generate SQL and parameters using AstToPreparedStatementSpecVisitor
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
