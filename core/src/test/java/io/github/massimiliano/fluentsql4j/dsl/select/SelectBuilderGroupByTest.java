package io.github.massimiliano.fluentsql4j.dsl.select;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderGroupByTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void singleColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\"");
    }

    @Test
    void multipleColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .column("product_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withTableAlias() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .groupBy()
                .column("customer_id")
                .column("product_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withQualifiedColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("orders", "customer_id")
                .column("orders", "product_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withAliasAndQualifiedColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .groupBy()
                .column("o", "customer_id")
                .column("o", "product_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"customer_id\", \"product_id\"");
    }

    @Test
    void withWhere() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("status")
                .eq("completed")
                .groupBy()
                .column("customer_id")
                .build()
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" WHERE \"status\" = ? GROUP BY \"customer_id\"");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
    }

    @Test
    void withOrderBy() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .orderBy()
                .asc("customer_id")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\" ORDER BY \"customer_id\" ASC");
    }

    @Test
    void withJoin() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .groupBy()
                .column("c", "id")
                .column("c", "name")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" GROUP BY \"c\".\"id\", \"c\".\"name\"");
    }

    @Test
    void withJoinWhereAndOrderBy() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .where()
                .column("o", "status")
                .eq("completed")
                .groupBy()
                .column("c", "id")
                .orderBy()
                .asc("c", "id")
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE \"o\".\"status\" = ? GROUP BY \"c\".\"id\" ORDER BY \"c\".\"id\" ASC");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
    }

    @Test
    void withFetchAndOffset() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .fetch(10)
                .offset(5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"customer_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void manyColumns() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .column("product_id")
                .column("region")
                .column("status")
                .column("payment_method")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT * FROM \"orders\" GROUP BY \"customer_id\", \"product_id\", \"region\", \"status\", \"payment_method\"");
    }

    @Test
    void complexQuery() throws SQLException {
        PreparedStatement result = new SelectBuilder(specFactory, "customer_id", "product_id", "total")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .column("total")
                .gt(100)
                .groupBy()
                .column("o", "customer_id")
                .column("o", "product_id")
                .orderBy()
                .desc("total")
                .fetch(20)
                .offset(10)
                .build(sqlCaptureHelper.getConnection());

        assertThat(result).isSameAs(sqlCaptureHelper.getPreparedStatement());
        assertThatSql(sqlCaptureHelper)
                .isEqualTo(
                        "SELECT \"o\".\"customer_id\", \"o\".\"product_id\", \"o\".\"total\" FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE (\"o\".\"status\" = ?) AND (\"o\".\"total\" > ?) GROUP BY \"o\".\"customer_id\", \"o\".\"product_id\" ORDER BY \"o\".\"total\" DESC OFFSET 10 ROWS FETCH NEXT 20 ROWS ONLY");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "completed");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
    }
}
