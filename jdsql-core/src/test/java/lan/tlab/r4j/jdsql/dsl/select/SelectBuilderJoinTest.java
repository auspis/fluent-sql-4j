package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SelectBuilderJoinTest {

    private DialectRenderer renderer;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        renderer = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void innerJoin() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\"");
    }

    @Test
    void leftJoin() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .leftJoin("profiles")
                .on("users.id", "profiles.user_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" LEFT JOIN \"profiles\" ON \"users\".\"id\" = \"profiles\".\"user_id\"");
    }

    @Test
    void rightJoin() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .rightJoin("departments")
                .on("users.dept_id", "departments.id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" RIGHT JOIN \"departments\" ON \"users\".\"dept_id\" = \"departments\".\"id\"");
    }

    @Test
    void fullJoin() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .fullJoin("roles")
                .on("users.role_id", "roles.id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"users\" FULL JOIN \"roles\" ON \"users\".\"role_id\" = \"roles\".\"id\"");
    }

    @Test
    void crossJoin() throws SQLException {
        new SelectBuilder(renderer, "*").from("users").crossJoin("settings").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM \"users\" CROSS JOIN \"settings\"");
    }

    @Test
    void innerJoinWithAlias() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void multipleJoins() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .leftJoin("products")
                .as("p")
                .on("o.product_id", "p.id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"products\" AS p ON \"o\".\"product_id\" = \"p\".\"id\"");
    }

    @Test
    void joinWithSelectedColumns() throws SQLException {
        new SelectBuilder(renderer, "name", "email", "order_id")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_id\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void joinWithWhereClause() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where()
                .column("status")
                .eq("active")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = ?");
        verify(ps).setObject(1, "active");
    }

    @Test
    void joinWithOrderBy() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .orderBy("created_at")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" ORDER BY \"users\".\"created_at\" ASC");
    }

    @Test
    void joinWithFetchAndOffset() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .fetch(10)
                .offset(5)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void complexJoinQuery() throws SQLException {
        new SelectBuilder(renderer, "name", "email", "order_total")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .leftJoin("payments")
                .as("p")
                .on("o.id", "p.order_id")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .column("amount")
                .gt(100)
                .orderByDesc("created_at")
                .fetch(20)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_total\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"payments\" AS p ON \"o\".\"id\" = \"p\".\"order_id\" WHERE (\"u\".\"status\" = ?) AND (\"u\".\"amount\" > ?) ORDER BY \"u\".\"created_at\" DESC FETCH NEXT 20 ROWS ONLY");
        verify(ps).setObject(1, "completed");
        verify(ps).setObject(2, 100);
    }

    @Test
    void joinWithoutFromThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*").innerJoin("orders"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("FROM table must be specified before adding a join");
    }

    @Test
    void joinWithEmptyLeftColumnThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .innerJoin("orders")
                        .on("", "orders.user_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Left column cannot be null or empty");
    }

    @Test
    void joinWithEmptyRightColumnThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .innerJoin("orders")
                        .on("users.id", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Right column cannot be null or empty");
    }

    @Test
    void joinWithEmptyAliasThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .innerJoin("orders")
                        .as(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty");
    }
}
