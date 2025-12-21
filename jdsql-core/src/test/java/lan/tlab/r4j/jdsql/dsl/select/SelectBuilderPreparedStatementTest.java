package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderPreparedStatementTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void buildPreparedStatementRequiresConnection() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("age")
                .gt(20);

        assertThatThrownBy(() -> builder.buildPreparedStatement(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void buildPreparedStatementCompilesWithoutError() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .where()
                .column("age")
                .gte(18)
                .and()
                .column("status")
                .eq("active")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT \"name\", \"email\" FROM \"users\" WHERE (\"age\" >= ?) AND (\"status\" = ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "active");
    }

    @Test
    void buildPreparedStatementWithJoinCompilesWithoutError() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "name", "email")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where()
                .column("status")
                .eq("active")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
    }
}
