package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderGroupByTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void singleColumn() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("customer_id")
                .build();

        assertThat(sql).isEqualTo("SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\"");
    }

    @Test
    void multipleColumns() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("customer_id", "product_id")
                .build();

        assertThat(sql)
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\", \"orders\".\"product_id\"");
    }

    @Test
    void withTableAlias() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .as("o")
                .groupBy("customer_id", "product_id")
                .build();

        assertThat(sql).isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"o\".\"customer_id\", \"o\".\"product_id\"");
    }

    @Test
    void withQualifiedColumns() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("orders.customer_id", "orders.product_id")
                .build();

        assertThat(sql)
                .isEqualTo("SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\", \"orders\".\"product_id\"");
    }

    @Test
    void withAliasAndQualifiedColumns() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .as("o")
                .groupBy("o.customer_id", "o.product_id")
                .build();

        assertThat(sql).isEqualTo("SELECT * FROM \"orders\" AS o GROUP BY \"o\".\"customer_id\", \"o\".\"product_id\"");
    }

    @Test
    void withWhere() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .where()
                .column("status")
                .eq("completed")
                .groupBy("customer_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" WHERE \"orders\".\"status\" = 'completed' GROUP BY \"orders\".\"customer_id\"");
    }

    @Test
    void withOrderBy() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("customer_id")
                .orderBy("customer_id")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\" ORDER BY \"orders\".\"customer_id\" ASC");
    }

    @Test
    void withJoin() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o.customer_id", "c.id")
                .groupBy("c.id", "c.name")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" GROUP BY \"c\".\"id\", \"c\".\"name\"");
    }

    @Test
    void withJoinWhereAndOrderBy() {
        String sql = new SelectBuilder(renderer, "*")
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
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE \"o\".\"status\" = 'completed' GROUP BY \"c\".\"id\" ORDER BY \"o\".\"c.id\" ASC");
    }

    @Test
    void withFetchAndOffset() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("customer_id")
                .fetch(10)
                .offset(5)
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\" OFFSET 5 ROWS FETCH NEXT 10 ROWS ONLY");
    }

    @Test
    void manyColumns() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .groupBy("customer_id", "product_id", "region", "status", "payment_method")
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT * FROM \"orders\" GROUP BY \"orders\".\"customer_id\", \"orders\".\"product_id\", \"orders\".\"region\", \"orders\".\"status\", \"orders\".\"payment_method\"");
    }

    @Test
    void complexQuery() {
        String sql = new SelectBuilder(renderer, "customer_id", "product_id", "total")
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
                .build();

        assertThat(sql)
                .isEqualTo(
                        "SELECT \"o\".\"customer_id\", \"o\".\"product_id\", \"o\".\"total\" FROM \"orders\" AS o INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" WHERE (\"o\".\"status\" = 'completed') AND (\"o\".\"total\" > 100) GROUP BY \"o\".\"customer_id\", \"o\".\"product_id\" ORDER BY \"o\".\"total\" DESC OFFSET 10 ROWS FETCH NEXT 20 ROWS ONLY");
    }

    @Test
    void noColumnsThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("orders").groupBy())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified for GROUP BY");
    }

    @Test
    void nullColumnsThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("orders").groupBy((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one column must be specified for GROUP BY");
    }

    @Test
    void emptyColumnThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("orders").groupBy("customer_id", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void nullColumnInArrayThrowsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("orders").groupBy("customer_id", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }
}
