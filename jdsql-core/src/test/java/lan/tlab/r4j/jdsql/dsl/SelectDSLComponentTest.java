package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.window.OverClause;
import lan.tlab.r4j.jdsql.ast.core.expression.window.WindowFunction;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.util.annotation.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@ComponentTest
class SelectDSLComponentTest {

    private DSL dsl;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSqlRendererFactory.dslStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void createsSelectBuilderWithRenderer() throws SQLException {
        dsl.select("name", "email").from("users").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "email" FROM "users"\
                """);
    }

    @Test
    void appliesRendererQuoting() throws SQLException {
        dsl.select("id", "value").from("temp_table").buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "id", "value" FROM "temp_table"\
                """);
    }

    @Test
    void fluentApiWithComplexQuery() throws SQLException {
        dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .and()
                .column("active")
                .eq(true)
                .orderBy("name")
                .fetch(10)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "age" \
                FROM "users" \
                WHERE ("age" > ?) AND ("active" = ?) \
                ORDER BY "name" ASC \
                FETCH NEXT 10 ROWS ONLY""");
        verify(ps).setObject(1, 18);
        verify(ps).setObject(2, true);
    }

    @Test
    void jsonFunctionsWithFluentApi() throws SQLException {
        dsl.select()
                .column("products", "name")
                .jsonExists("products", "metadata", "$.tags")
                .as("has_tags")
                .jsonValue("products", "metadata", "$.price")
                .returning("DECIMAL(10,2)")
                .as("price")
                .jsonQuery("products", "metadata", "$.details")
                .as("details")
                .from("products")
                .where()
                .column("category")
                .eq("Electronics")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", \
                JSON_EXISTS("metadata", ?) AS "has_tags", \
                JSON_VALUE("metadata", ? RETURNING DECIMAL(10,2)) AS "price", \
                JSON_QUERY("metadata", ?) AS "details" \
                FROM "products" \
                WHERE "category" = ?\
                """);
        verify(ps).setObject(1, "$.tags");
        verify(ps).setObject(2, "$.price");
        verify(ps).setObject(3, "$.details");
        verify(ps).setObject(4, "Electronics");
    }

    @Test
    void jsonValueWithDefaultBehaviorFluentApi() throws SQLException {
        dsl.select()
                .column("products", "id")
                .column("products", "name")
                .jsonValue("products", "data", "$.discount")
                .returning("DECIMAL(5,2)")
                .defaultOnEmpty("0.00")
                .as("discount")
                .from("products")
                .orderBy("name")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "id", "name", \
                JSON_VALUE("data", ? RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS "discount" \
                FROM "products" \
                ORDER BY "name" ASC\
                """);
        verify(ps).setObject(1, "$.discount");
    }

    @Test
    void jsonFunctionsUsingExpressionApi() throws SQLException {
        // Test the low-level expression API for custom JSON configurations
        JsonValue jsonDiscount = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(5,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                null);

        dsl.select()
                .column("products", "name")
                .expression(jsonDiscount, "discount")
                .from("products")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", \
                JSON_VALUE("data", ? RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS "discount" \
                FROM "products"\
                """);
        verify(ps).setObject(1, "$.discount");
    }

    @Test
    void windowFunctionRowNumberWithFluentApi() throws SQLException {
        dsl.select()
                .column("employees", "name")
                .column("employees", "salary")
                .rowNumber()
                .orderByDesc("employees", "salary")
                .as("rank")
                .from("employees")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "salary", \
                ROW_NUMBER() OVER (ORDER BY "salary" DESC) AS "rank" \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionRowNumberWithPartitionBy() throws SQLException {
        dsl.select()
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .rowNumber()
                .partitionBy("employees", "department")
                .orderByDesc("employees", "salary")
                .as("dept_rank")
                .from("employees")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "department", "salary", \
                ROW_NUMBER() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_rank" \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionRankAndDenseRank() throws SQLException {
        dsl.select()
                .column("products", "name")
                .column("products", "price")
                .rank()
                .orderByDesc("products", "price")
                .as("price_rank")
                .denseRank()
                .orderByDesc("products", "price")
                .as("price_dense_rank")
                .from("products")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "price", \
                RANK() OVER (ORDER BY "price" DESC) AS "price_rank", \
                DENSE_RANK() OVER (ORDER BY "price" DESC) AS "price_dense_rank" \
                FROM "products"\
                """);
    }

    @Test
    void windowFunctionLagWithFluentApi() throws SQLException {
        dsl.select()
                .column("sales", "sale_date")
                .column("sales", "amount")
                .lag("sales", "amount", 1)
                .orderByAsc("sales", "sale_date")
                .as("previous_amount")
                .from("sales")
                .where()
                .column("year")
                .eq(2024)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "sale_date", "amount", \
                LAG("amount", 1) OVER (ORDER BY "sale_date" ASC) AS "previous_amount" \
                FROM "sales" \
                WHERE "year" = ?\
                """);
        verify(ps).setObject(1, 2024);
    }

    @Test
    void windowFunctionUsingExpressionApi() throws SQLException {
        // Test the low-level expression API for custom window function configurations
        dsl.select()
                .column("employees", "name")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "rank")
                .from("employees")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", \
                ROW_NUMBER() OVER (ORDER BY "salary" DESC) AS "rank" \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionNtileWithFluentApi() throws SQLException {
        dsl.select()
                .column("employees", "name")
                .column("employees", "salary")
                .ntile(4)
                .orderByDesc("employees", "salary")
                .as("quartile")
                .from("employees")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "salary", \
                NTILE(4) OVER (ORDER BY "salary" DESC) AS "quartile" \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionLeadWithFluentApi() throws SQLException {
        dsl.select()
                .column("sales", "sale_date")
                .lead("sales", "amount", 1)
                .orderByAsc("sales", "sale_date")
                .as("next_amount")
                .column("sales", "amount")
                .from("sales")
                .where()
                .column("year")
                .eq(2024)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "sale_date", \
                LEAD("amount", 1) OVER (ORDER BY "sale_date" ASC) AS "next_amount", "amount" \
                FROM "sales" \
                WHERE "year" = ?\
                """);
        verify(ps).setObject(1, 2024);
    }

    @Test
    void windowFunctionWithComplexPartitioningAndOrdering() throws SQLException {
        dsl.select()
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .rowNumber()
                .partitionBy("employees", "department")
                .orderByDesc("employees", "salary")
                .as("dept_rank")
                .rank()
                .partitionBy("employees", "department")
                .orderByDesc("employees", "salary")
                .as("dept_rank_with_gaps")
                .from("employees")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name", "department", "salary", \
                ROW_NUMBER() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_rank", \
                RANK() OVER (PARTITION BY "department" ORDER BY "salary" DESC) AS "dept_rank_with_gaps" \
                FROM "employees"\
                """);
    }

    @Test
    void innerJoinWithMultipleConditions() throws SQLException {
        dsl.select("name", "email", "created_at")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where()
                .column("status")
                .eq("active")
                .and()
                .column("email")
                .like("%@example.com")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "u"."name", "u"."email", "u"."created_at" \
                FROM "users" AS u \
                INNER JOIN "orders" AS o ON "u"."id" = "o"."user_id" \
                WHERE ("u"."status" = ?) AND ("u"."email" LIKE ?)\
                """);
        verify(ps).setObject(1, "active");
        verify(ps).setObject(2, "%@example.com");
    }

    @Test
    void leftJoinWithWhereAndOrderBy() throws SQLException {
        dsl.select("name", "email")
                .from("users")
                .as("u")
                .leftJoin("profiles")
                .as("p")
                .on("u.id", "p.user_id")
                .where()
                .column("active")
                .eq(true)
                .orderByDesc("created_at")
                .fetch(10)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "u"."name", "u"."email" \
                FROM "users" AS u \
                LEFT JOIN "profiles" AS p ON "u"."id" = "p"."user_id" \
                WHERE "u"."active" = ? \
                ORDER BY "u"."created_at" DESC \
                FETCH NEXT 10 ROWS ONLY\
                """);
        verify(ps).setObject(1, true);
    }

    @Test
    void multipleJoinsWithGroupByHaving() throws SQLException {
        dsl.select("name", "city")
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
                .eq("active")
                .groupBy("name", "city")
                .having()
                .column("city")
                .like("New%")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "u"."name", "u"."city" \
                FROM "users" AS u \
                INNER JOIN "orders" AS o ON "u"."id" = "o"."user_id" \
                LEFT JOIN "payments" AS p ON "o"."id" = "p"."order_id" \
                WHERE "u"."status" = ? \
                GROUP BY "u"."name", "u"."city" \
                HAVING "u"."city" LIKE ?\
                """);
        verify(ps).setObject(1, "active");
        verify(ps).setObject(2, "New%");
    }

    @Test
    void rightJoinWithComplexWhereConditions() throws SQLException {
        dsl.select("dept_name", "budget", "location")
                .from("departments")
                .as("d")
                .rightJoin("employees")
                .as("e")
                .on("d.id", "e.dept_id")
                .where()
                .column("budget")
                .between(50000, 100000)
                .and()
                .column("active")
                .eq(true)
                .or()
                .column("manager_id")
                .isNull()
                .orderByDesc("budget")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "d"."dept_name", "d"."budget", "d"."location" \
                FROM "departments" AS d \
                RIGHT JOIN "employees" AS e ON "d"."id" = "e"."dept_id" \
                WHERE (("d"."budget" BETWEEN ? AND ?) AND ("d"."active" = ?)) OR ("d"."manager_id" IS NULL) \
                ORDER BY "d"."budget" DESC\
                """);
        verify(ps).setObject(1, 50000);
        verify(ps).setObject(2, 100000);
        verify(ps).setObject(3, true);
    }

    @Test
    void groupByWithMultipleAggregationsAndHaving() throws SQLException {
        dsl.select("department", "COUNT(*)", "SUM(salary)", "AVG(age)")
                .from("employees")
                .as("e")
                .where()
                .column("status")
                .eq("active")
                .groupBy("department")
                .having()
                .column("department")
                .like("Engineering%")
                .orderBy("department")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "department", "COUNT(*)", "SUM(salary)", "AVG(age)" \
                FROM "employees" AS e \
                WHERE "status" = ? \
                GROUP BY "department" \
                HAVING "department" LIKE ? \
                ORDER BY "department" ASC\
                """);
        verify(ps).setObject(1, "active");
        verify(ps).setObject(2, "Engineering%");
    }

    @Test
    void groupByMultipleColumnsWithHavingComplexConditions() throws SQLException {
        dsl.select("region", "category", "COUNT(*)", "MAX(price)")
                .from("products")
                .as("p")
                .groupBy("region", "category")
                .having()
                .column("region")
                .in("North", "South", "East")
                .andHaving()
                .column("category")
                .ne("discontinued")
                .orderBy("region")
                .orderBy("category")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "region", "category", "COUNT(*)", "MAX(price)" \
                FROM "products" AS p \
                GROUP BY "region", "category" \
                HAVING ("region" IN (?, ?, ?)) AND ("category" <> ?) \
                ORDER BY "category" ASC\
                """);
        verify(ps).setObject(1, "North");
        verify(ps).setObject(2, "South");
        verify(ps).setObject(3, "East");
        verify(ps).setObject(4, "discontinued");
    }

    @Test
    void groupByWithWhereHavingOrderByFetch() throws SQLException {
        dsl.select("customer_id", "COUNT(*)", "SUM(amount)", "AVG(quantity)")
                .from("orders")
                .as("o")
                .where()
                .column("order_date")
                .between(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .gt(1000)
                .andHaving()
                .column("customer_id")
                .lt(9999)
                .orderByDesc("customer_id")
                .fetch(10)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "customer_id", "COUNT(*)", "SUM(amount)", "AVG(quantity)" \
                FROM "orders" AS o \
                WHERE "order_date" BETWEEN ? AND ? \
                GROUP BY "customer_id" \
                HAVING ("customer_id" > ?) AND ("customer_id" < ?) \
                ORDER BY "customer_id" DESC \
                FETCH NEXT 10 ROWS ONLY\
                """);
        verify(ps).setObject(1, LocalDate.of(2023, 1, 1));
        verify(ps).setObject(2, LocalDate.of(2023, 12, 31));
        verify(ps).setObject(3, 1000);
        verify(ps).setObject(4, 9999);
    }

    @Test
    void whereWithJsonValueComparison() throws SQLException {
        dsl.select("id", "name")
                .from("users")
                .where()
                .jsonValue("profile", "$.city")
                .eq("Rome")
                .and()
                .jsonValue("profile", "$.age")
                .gt(25)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "id", "name" \
                FROM "users" \
                WHERE (JSON_VALUE("profile", ?) = ?) \
                AND (JSON_VALUE("profile", ?) > ?)\
                """);
        verify(ps).setObject(1, "$.city");
        verify(ps).setObject(2, "Rome");
        verify(ps).setObject(3, "$.age");
        verify(ps).setObject(4, 25);
    }

    @Test
    void whereWithJsonExistsCondition() throws SQLException {
        dsl.select("product_id", "name", "price")
                .from("products")
                .where()
                .jsonExists("metadata", "$.featured")
                .exists()
                .and()
                .column("active")
                .eq(true)
                .orderBy("name")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "product_id", "name", "price" \
                FROM "products" \
                WHERE (JSON_EXISTS("metadata", ?) = ?) \
                AND ("active" = ?) \
                ORDER BY "name" ASC\
                """);
        verify(ps).setObject(1, "$.featured");
        verify(ps).setObject(2, true);
        verify(ps).setObject(3, true);
    }

    @Test
    void whereWithMixedJsonFunctionsAndRegularColumns() throws SQLException {
        dsl.select("*")
                .from("orders")
                .as("o")
                .where()
                .column("status")
                .eq("completed")
                .and()
                .jsonValue("o", "details", "$.payment.method")
                .eq("credit_card")
                .or()
                .jsonExists("o", "details", "$.discount")
                .notExists()
                .orderByDesc("order_date")
                .fetch(20)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT * \
                FROM "orders" AS o \
                WHERE (("status" = ?) \
                AND (JSON_VALUE("details", ?) = ?)) \
                OR (JSON_EXISTS("details", ?) = ?) \
                ORDER BY "order_date" DESC \
                FETCH NEXT 20 ROWS ONLY\
                """);
        verify(ps).setObject(1, "completed");
        verify(ps).setObject(2, "$.payment.method");
        verify(ps).setObject(3, "credit_card");
        verify(ps).setObject(4, "$.discount");
        verify(ps).setObject(5, false);
    }
}
