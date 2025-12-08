package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SelectBuilderPreparedStatementTest {

    private PreparedStatementSpecFactory specFactory;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
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
                .buildPreparedStatement(connection);

        assertThat(result).isSameAs(ps);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT \"name\", \"email\" FROM \"users\" WHERE (\"age\" >= ?) AND (\"status\" = ?)");
        verify(ps).setObject(1, 18);
        verify(ps).setObject(2, "active");
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
                .buildPreparedStatement(connection);

        assertThat(result).isSameAs(ps);
        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = ?");
        verify(ps).setObject(1, "active");
    }
}
