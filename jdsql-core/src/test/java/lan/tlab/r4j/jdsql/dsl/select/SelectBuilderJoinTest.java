package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderJoinTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void innerJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .innerJoin("orders")
                .on("users", "id", "orders", "user_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\"");
    }

    @Test
    void leftJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .leftJoin("profiles")
                .on("users", "id", "profiles", "user_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" LEFT JOIN \"profiles\" ON \"users\".\"id\" = \"profiles\".\"user_id\"");
    }

    @Test
    void rightJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .rightJoin("departments")
                .on("users", "dept_id", "departments", "id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" RIGHT JOIN \"departments\" ON \"users\".\"dept_id\" = \"departments\".\"id\"");
    }

    @Test
    void fullJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .fullJoin("roles")
                .on("users", "role_id", "roles", "id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"users\" FULL JOIN \"roles\" ON \"users\".\"role_id\" = \"roles\".\"id\"");
    }

    @Test
    void crossJoin() throws SQLException {
        new SelectBuilder(specFactory, "*").from("users").crossJoin("settings").build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"users\" CROSS JOIN \"settings\"");
    }

    @Test
    void innerJoinWithAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void multipleJoins() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .leftJoin("products")
                .as("p")
                .on("o", "product_id", "p", "id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"products\" AS p ON \"o\".\"product_id\" = \"p\".\"id\"");
    }

    @Test
    void joinWithSelectedColumns() throws SQLException {
        new SelectBuilder(specFactory, "name", "email", "order_id")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_id\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void joinWithWhereClause() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .where()
                .column("status")
                .eq("active")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
    }

    @Test
    void joinWithOrderBy() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .innerJoin("orders")
                .on("users", "id", "orders", "user_id")
                .orderBy("created_at")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" ORDER BY \"users\".\"created_at\" ASC");
    }

    @Test
    void joinWithFetchAndOffset() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .innerJoin("orders")
                .on("users", "id", "orders", "user_id")
                .fetch(10)
                .offset(5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void complexJoinQuery() throws SQLException {
        new SelectBuilder(specFactory, "name", "email", "order_total")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .leftJoin("payments")
                .as("p")
                .on("o", "id", "p", "order_id")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .column("amount")
                .gt(100)
                .orderByDesc("created_at")
                .fetch(20)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_total\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"payments\" AS p ON \"o\".\"id\" = \"p\".\"order_id\" WHERE (\"u\".\"status\" = ?) AND (\"u\".\"amount\" > ?) ORDER BY \"u\".\"created_at\" DESC FETCH NEXT 20 ROWS ONLY");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
    }

    @Test
    void joinWithoutFromThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*").innerJoin("orders"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("FROM table must be specified before adding a join");
    }

    @Test
    void joinWithEmptyLeftColumnThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .innerJoin("orders")
                        .on("users", "", "orders", "user_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Left column cannot be null or empty");
    }

    @Test
    void joinWithEmptyRightColumnThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .innerJoin("orders")
                        .on("users", "id", "orders", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Right column cannot be null or empty");
    }

    @Test
    void joinWithEmptyAliasThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .innerJoin("orders")
                        .as(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty");
    }
}
