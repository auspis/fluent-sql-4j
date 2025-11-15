package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderJoinTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void innerJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\"");
    }

    @Test
    void leftJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .leftJoin("profiles")
                .on("users.id", "profiles.user_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" LEFT JOIN \"profiles\" ON \"users\".\"id\" = \"profiles\".\"user_id\"");
    }

    @Test
    void rightJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .rightJoin("departments")
                .on("users.dept_id", "departments.id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" RIGHT JOIN \"departments\" ON \"users\".\"dept_id\" = \"departments\".\"id\"");
    }

    @Test
    void fullJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .fullJoin("roles")
                .on("users.role_id", "roles.id")
                .build();

        assertThat(sql)
                .isEqualTo("SELECT * FROM \"users\" FULL JOIN \"roles\" ON \"users\".\"role_id\" = \"roles\".\"id\"");
    }

    @Test
    void crossJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .crossJoin("settings")
                .build();

        assertThat(sql).isEqualTo("SELECT * FROM \"users\" CROSS JOIN \"settings\"");
    }

    @Test
    void innerJoinWithAlias() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void multipleJoins() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .leftJoin("products")
                .as("p")
                .on("o.product_id", "p.id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"products\" AS p ON \"o\".\"product_id\" = \"p\".\"id\"");
    }

    @Test
    void joinWithSelectedColumns() {
        String sql = new SelectBuilder(renderer, "name", "email", "order_id")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_id\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\"");
    }

    @Test
    void joinWithWhereClause() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where()
                .column("status")
                .eq("active")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" WHERE \"u\".\"status\" = 'active'");
    }

    @Test
    void joinWithOrderBy() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .orderBy("created_at")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" ORDER BY \"users\".\"created_at\" ASC");
    }

    @Test
    void joinWithFetchAndOffset() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .innerJoin("orders")
                .on("users.id", "orders.user_id")
                .fetch(10)
                .offset(5)
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"users\" INNER JOIN \"orders\" ON \"users\".\"id\" = \"orders\".\"user_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void complexJoinQuery() {
        String sql = new SelectBuilder(renderer, "name", "email", "order_total")
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
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT \"u\".\"name\", \"u\".\"email\", \"u\".\"order_total\" FROM \"users\" AS u INNER JOIN \"orders\" AS o ON \"u\".\"id\" = \"o\".\"user_id\" LEFT JOIN \"payments\" AS p ON \"o\".\"id\" = \"p\".\"order_id\" WHERE (\"u\".\"status\" = 'completed') AND (\"u\".\"amount\" > 100) ORDER BY \"u\".\"created_at\" DESC OFFSET 0 ROWS FETCH NEXT 20 ROWS ONLY");
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
