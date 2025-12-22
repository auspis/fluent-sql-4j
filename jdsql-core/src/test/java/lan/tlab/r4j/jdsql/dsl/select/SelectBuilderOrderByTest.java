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

class SelectBuilderOrderByTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void singleColumnAscending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .asc("customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"customer_id\" ASC");
    }

    @Test
    void singleColumnDescending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .orderBy()
                .desc("status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" ORDER BY \"status\" DESC");
    }

    @Test
    void withTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withExplicitAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("o", "customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withExplicitAliasDescending() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .desc("o", "status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"status\" DESC");
    }

    @Test
    void multipleColumnsWithExplicitAliases() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .orderBy()
                .asc("o", "customer_id")
                .desc("o", "created_at")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" AS o ORDER BY \"customer_id\" ASC, \"created_at\" DESC");
    }

    @Test
    void withJoin() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .orderBy()
                .asc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" ORDER BY \"c\".\"name\" ASC");
    }

    @Test
    void withJoinMultipleColumns() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .orderBy()
                .asc("o", "created_at")
                .desc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" ORDER BY \"o\".\"created_at\" ASC, \"c\".\"name\" DESC");
    }

    @Test
    void withWhereAndJoin() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .where()
                .column("o", "status")
                .eq("completed")
                .orderBy()
                .asc("c", "name")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE \"o\".\"status\" = ? ORDER BY \"c\".\"name\" ASC");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
    }

    @Test
    void complexQueryWithGroupByAndOrderBy() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "customer_id", "status")
                .from("orders")
                .as("o")
                .groupBy()
                .column("customer_id")
                .column("status")
                .build()
                .orderBy()
                .asc("customer_id")
                .desc("status")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"customer_id\", \"status\" FROM \"orders\" AS o GROUP BY \"customer_id\", \"status\" ORDER BY \"customer_id\" ASC, \"status\" DESC");
    }

    // Validation tests

    @Test
    void nullColumnThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().asc(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column cannot be null or empty");
    }

    @Test
    void emptyColumnThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().asc("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column cannot be null or empty");
    }

    @Test
    void dotNotationInColumnThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().asc("orders.customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column must not contain dot notation")
                .hasMessageContaining("Use asc(alias, column)");
    }

    @Test
    void nullColumnDescThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().desc(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column cannot be null or empty");
    }

    @Test
    void dotNotationInColumnDescThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().desc("orders.status"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column must not contain dot notation")
                .hasMessageContaining("Use desc(alias, column)");
    }

    @Test
    void nullAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc(null, "customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY alias cannot be null or empty");
    }

    @Test
    void emptyAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc("  ", "customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY alias cannot be null or empty");
    }

    @Test
    void dotNotationInAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc("o.x", "customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY alias must not contain dot notation");
    }

    @Test
    void nullColumnWithAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc("o", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column cannot be null or empty");
    }

    @Test
    void emptyColumnWithAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc("o", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column cannot be null or empty");
    }

    @Test
    void dotNotationInColumnWithAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().asc("o", "x.customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column must not contain dot notation")
                .hasMessageContaining("Use asc(alias, column) with separate parameters");
    }

    @Test
    void nullAliasDescThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().desc(null, "customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY alias cannot be null or empty");
    }

    @Test
    void dotNotationInAliasDescThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().desc("o.x", "customer_id"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY alias must not contain dot notation");
    }

    @Test
    void dotNotationInColumnDescWithAliasThrowsException() {
        SelectBuilder builder =
                new SelectBuilder(specFactory, "*").from("orders").as("o");

        assertThatThrownBy(() -> builder.orderBy().desc("o", "x.status"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ORDER BY column must not contain dot notation")
                .hasMessageContaining("Use desc(alias, column) with separate parameters");
    }

    @Test
    void emptyOrderByThrowsException() {
        SelectBuilder builder = new SelectBuilder(specFactory, "*").from("orders");

        assertThatThrownBy(() -> builder.orderBy().build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ORDER BY must contain at least one sorting column");
    }
}
