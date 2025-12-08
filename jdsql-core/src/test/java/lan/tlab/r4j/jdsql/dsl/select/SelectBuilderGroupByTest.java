package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SelectBuilderGroupByTest {

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
    void singleColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\"");
    }

    @Test
    void multipleColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id", "product_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .groupBy("customer_id", "product_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withQualifiedColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("orders.customer_id", "orders.product_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withAliasAndQualifiedColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .groupBy("o.customer_id", "o.product_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withWhere() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("status")
                .eq("completed")
                .groupBy("customer_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"orders\" WHERE \"status\" = ? GROUP BY \"customer_id\"");
    }

    @Test
    void withOrderBy() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id")
                .orderBy("customer_id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\" ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .groupBy("c.id", "c.name")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" GROUP BY \"c\".\"id\", \"c\".\"name\"");
    }

    @Test
    void withJoinWhereAndOrderBy() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .where()
                .column("status")
                .eq("completed")
                .groupBy("c.id")
                .orderBy("c.id")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE \"o\".\"status\" = ? GROUP BY \"c\".\"id\" ORDER BY \"o\".\"c.id\" ASC");
    }

    @Test
    void withFetchAndOffset() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id")
                .fetch(10)
                .offset(5)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void manyColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy("customer_id", "product_id", "region", "status", "payment_method")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\", \"region\", \"status\", \"payment_method\"");
    }

    @Test
    void complexQuery() throws SQLException {
        new SelectBuilder(specFactory, "customer_id", "product_id", "total")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .column("total")
                .gt(100)
                .groupBy("o.customer_id", "o.product_id")
                .orderByDesc("total")
                .fetch(20)
                .offset(10)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        "SELECT \"o\".\"customer_id\", \"o\".\"product_id\", \"o\".\"total\" FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE (\"o\".\"status\" = ?) AND (\"o\".\"total\" > ?) GROUP BY \"o\".\"customer_id\", \"o\".\"product_id\" ORDER BY \"o\".\"total\" DESC OFFSET 10 ROWS FETCH NEXT 20 ROWS ONLY");
    }

    @Test
    void noColumnsThrowsException() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("orders").groupBy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified for GROUP BY");
    }

    @Test
    void nullColumnsThrowsException() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("orders").groupBy((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified for GROUP BY");
    }

    @Test
    void emptyColumnThrowsException() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("orders").groupBy("customer_id", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void nullColumnInArrayThrowsException() {
        assertThatThrownBy(
                        () -> new SelectBuilder(specFactory, "*").from("orders").groupBy("customer_id", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }
}
