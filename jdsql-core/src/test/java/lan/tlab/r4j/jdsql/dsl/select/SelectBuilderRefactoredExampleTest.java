package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Example test demonstrating the use of MockedConnectionHelper and SqlAssert.
 * <p>
 * This is a refactored version showing how to reduce boilerplate setup code.
 */
class SelectBuilderRefactoredExampleTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void simpleSelect() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());

        // Using SqlAssert - more fluent than plain assertThat
        assertThatSql(sqlCaptureHelper.getSql()).isEqualTo("SELECT \"name\", \"email\" FROM \"users\"");
    }

    @Test
    void selectWithWhere() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // Using SqlAssert with fluent API
        assertThatSql(sqlCaptureHelper.getSql())
                .contains("SELECT *")
                .contains("FROM \"users\"")
                .contains("WHERE \"age\" > ?");

        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
    }

    @Test
    void selectWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("active")
                .eq(true)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // Using SqlAssert containsInOrder to verify SQL structure
        assertThatSql(sqlCaptureHelper.getSql())
                .containsInOrder("SELECT", "FROM \"users\"", "WHERE", "age", "AND", "active");

        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void selectWithNormalizedWhitespace() throws SQLException {
        new SelectBuilder(specFactory, "id", "name")
                .from("products")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // This works even if the actual SQL has extra whitespace
        assertThatSql(sqlCaptureHelper.getSql())
                .isEqualToNormalizingWhitespace("SELECT \"id\", \"name\"   FROM   \"products\"");
    }
}
