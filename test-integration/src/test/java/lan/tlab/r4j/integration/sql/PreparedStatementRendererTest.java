package lan.tlab.r4j.integration.sql;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PreparedStatementRendererTest {

    private Connection connection;
    private PreparedStatementRenderer renderer;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        renderer = new PreparedStatementRenderer();

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
        PsDto result = selectStmt.accept(renderer, context);

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
