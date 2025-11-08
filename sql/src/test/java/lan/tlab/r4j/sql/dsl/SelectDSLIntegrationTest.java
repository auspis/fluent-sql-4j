package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.OverClause;
import lan.tlab.r4j.sql.ast.common.expression.scalar.window.WindowFunction;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import lan.tlab.r4j.sql.util.annotation.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class SelectDSLIntegrationTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void createsSelectBuilderWithRenderer() {
        String result = dsl.select("name", "email").from("users").build();

        assertThat(result).isEqualTo("""
                SELECT "users"."name", "users"."email" FROM "users\"""");
    }

    @Test
    void appliesRendererQuoting() {
        String result = dsl.select("id", "value").from("temp_table").build();

        assertThat(result)
                .isEqualTo("""
                SELECT "temp_table"."id", "temp_table"."value" FROM "temp_table\"""");
    }

    @Test
    void fluentApiWithComplexQuery() {
        String result = dsl.select("name", "age")
                .from("users")
                .where("age")
                .gt(18)
                .and("active")
                .eq(true)
                .orderBy("name")
                .fetch(10)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "users"."name", "users"."age" \
                FROM "users" \
                WHERE ("users"."age" > 18) AND ("users"."active" = true) \
                ORDER BY "users"."name" ASC \
                OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY""");
    }

    @Test
    void jsonFunctionsWithFluentApi() {
        String result = dsl.select()
                .column("products", "name")
                .jsonExists("products", "metadata", "$.tags")
                .as("has_tags")
                .jsonValue("products", "metadata", "$.price")
                .returning("DECIMAL(10,2)")
                .as("price")
                .jsonQuery("products", "metadata", "$.details")
                .as("details")
                .from("products")
                .where("category")
                .eq("Electronics")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", \
                JSON_EXISTS("products"."metadata", '$.tags') AS has_tags, \
                JSON_VALUE("products"."metadata", '$.price' RETURNING DECIMAL(10,2)) AS price, \
                JSON_QUERY("products"."metadata", '$.details') AS details \
                FROM "products" \
                WHERE "products"."category" = 'Electronics'\
                """);
    }

    @Test
    void jsonValueWithDefaultBehaviorFluentApi() {
        String result = dsl.select()
                .column("products", "id")
                .column("products", "name")
                .jsonValue("products", "data", "$.discount")
                .returning("DECIMAL(5,2)")
                .defaultOnEmpty("0.00")
                .as("discount")
                .from("products")
                .orderBy("name")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."id", "products"."name", \
                JSON_VALUE("products"."data", '$.discount' RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS discount \
                FROM "products" \
                ORDER BY "products"."name" ASC\
                """);
    }

    @Test
    void jsonFunctionsUsingExpressionApi() {
        // Test the low-level expression API for custom JSON configurations
        JsonValue jsonDiscount = new JsonValue(
                ColumnReference.of("products", "data"),
                Literal.of("$.discount"),
                "DECIMAL(5,2)",
                OnEmptyBehavior.defaultValue("0.00"),
                null);

        String result = dsl.select()
                .column("products", "name")
                .expression(jsonDiscount, "discount")
                .from("products")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", \
                JSON_VALUE("products"."data", '$.discount' RETURNING DECIMAL(5,2) DEFAULT 0.00 ON EMPTY) AS discount \
                FROM "products"\
                """);
    }

    @Test
    void windowFunctionRowNumberWithFluentApi() {
        String result = dsl.select()
                .column("employees", "name")
                .column("employees", "salary")
                .rowNumber()
                .orderByDesc("employees", "salary")
                .as("rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "employees"."name", "employees"."salary", \
                ROW_NUMBER() OVER (ORDER BY "employees"."salary" DESC) AS rank \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionRowNumberWithPartitionBy() {
        String result = dsl.select()
                .column("employees", "name")
                .column("employees", "department")
                .column("employees", "salary")
                .rowNumber()
                .partitionBy("employees", "department")
                .orderByDesc("employees", "salary")
                .as("dept_rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "employees"."name", "employees"."department", "employees"."salary", \
                ROW_NUMBER() OVER (PARTITION BY "employees"."department" ORDER BY "employees"."salary" DESC) AS dept_rank \
                FROM "employees"\
                """);
    }

    @Test
    void windowFunctionRankAndDenseRank() {
        String result = dsl.select()
                .column("products", "name")
                .column("products", "price")
                .rank()
                .orderByDesc("products", "price")
                .as("price_rank")
                .denseRank()
                .orderByDesc("products", "price")
                .as("price_dense_rank")
                .from("products")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "products"."name", "products"."price", \
                RANK() OVER (ORDER BY "products"."price" DESC) AS price_rank, \
                DENSE_RANK() OVER (ORDER BY "products"."price" DESC) AS price_dense_rank \
                FROM "products"\
                """);
    }

    @Test
    void windowFunctionLagWithFluentApi() {
        String result = dsl.select()
                .column("sales", "sale_date")
                .column("sales", "amount")
                .lag("sales", "amount", 1)
                .orderByAsc("sales", "sale_date")
                .as("previous_amount")
                .from("sales")
                .where("year")
                .eq(2024)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "sales"."sale_date", "sales"."amount", \
                LAG("sales"."amount", 1) OVER (ORDER BY "sales"."sale_date" ASC) AS previous_amount \
                FROM "sales" \
                WHERE "sales"."year" = 2024\
                """);
    }

    @Test
    void windowFunctionUsingExpressionApi() {
        // Test the low-level expression API for custom window function configurations
        String result = dsl.select()
                .column("employees", "name")
                .expression(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "rank")
                .from("employees")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "employees"."name", \
                ROW_NUMBER() OVER (ORDER BY "employees"."salary" DESC) AS rank \
                FROM "employees"\
                """);
    }

    @Test
    void innerJoinWithMultipleConditions() {
        String result = dsl.select("name", "email", "created_at")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .where("status")
                .eq("active")
                .and("email")
                .like("%@example.com")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "u"."name", "u"."email", "u"."created_at" \
                FROM "users" AS u \
                INNER JOIN "orders" AS o ON "u"."id" = "o"."user_id" \
                WHERE ("u"."status" = 'active') AND ("u"."email" LIKE '%@example.com')\
                """);
    }

    @Test
    void leftJoinWithWhereAndOrderBy() {
        String result = dsl.select("name", "email")
                .from("users")
                .as("u")
                .leftJoin("profiles")
                .as("p")
                .on("u.id", "p.user_id")
                .where("active")
                .eq(true)
                .orderByDesc("created_at")
                .fetch(10)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "u"."name", "u"."email" \
                FROM "users" AS u \
                LEFT JOIN "profiles" AS p ON "u"."id" = "p"."user_id" \
                WHERE "u"."active" = true \
                ORDER BY "u"."created_at" DESC \
                OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY\
                """);
    }

    @Test
    void multipleJoinsWithGroupByHaving() {
        String result = dsl.select("name", "city")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u.id", "o.user_id")
                .leftJoin("payments")
                .as("p")
                .on("o.id", "p.order_id")
                .where("status")
                .eq("active")
                .groupBy("name", "city")
                .having("city")
                .like("New%")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "u"."name", "u"."city" \
                FROM "users" AS u \
                INNER JOIN "orders" AS o ON "u"."id" = "o"."user_id" \
                LEFT JOIN "payments" AS p ON "o"."id" = "p"."order_id" \
                WHERE "u"."status" = 'active' \
                GROUP BY "u"."name", "u"."city" \
                HAVING "u"."city" LIKE 'New%'\
                """);
    }

    @Test
    void rightJoinWithComplexWhereConditions() {
        String result = dsl.select("dept_name", "budget", "location")
                .from("departments")
                .as("d")
                .rightJoin("employees")
                .as("e")
                .on("d.id", "e.dept_id")
                .where("budget")
                .between(50000, 100000)
                .and("active")
                .eq(true)
                .or("manager_id")
                .isNull()
                .orderByDesc("budget")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "d"."dept_name", "d"."budget", "d"."location" \
                FROM "departments" AS d \
                RIGHT JOIN "employees" AS e ON "d"."id" = "e"."dept_id" \
                WHERE ((("d"."budget" >= 50000) AND ("d"."budget" <= 100000)) AND ("d"."active" = true)) OR ("d"."manager_id" IS NULL) \
                ORDER BY "d"."budget" DESC\
                """);
    }

    @Test
    void groupByWithMultipleAggregationsAndHaving() {
        String result = dsl.select("department", "COUNT(*)", "SUM(salary)", "AVG(age)")
                .from("employees")
                .as("e")
                .where("status")
                .eq("active")
                .groupBy("department")
                .having("department")
                .like("Engineering%")
                .orderBy("department")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "e"."department", "e"."COUNT(*)", "e"."SUM(salary)", "e"."AVG(age)" \
                FROM "employees" AS e \
                WHERE "e"."status" = 'active' \
                GROUP BY "e"."department" \
                HAVING "e"."department" LIKE 'Engineering%' \
                ORDER BY "e"."department" ASC\
                """);
    }

    @Test
    void groupByMultipleColumnsWithHavingComplexConditions() {
        String result = dsl.select("region", "category", "COUNT(*)", "MAX(price)")
                .from("products")
                .as("p")
                .groupBy("region", "category")
                .having("region")
                .in("North", "South", "East")
                .andHaving("category")
                .ne("discontinued")
                .orderBy("region")
                .orderBy("category")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "p"."region", "p"."category", "p"."COUNT(*)", "p"."MAX(price)" \
                FROM "products" AS p \
                GROUP BY "p"."region", "p"."category" \
                HAVING ("p"."region" IN('North', 'South', 'East')) AND ("p"."category" != 'discontinued') \
                ORDER BY "p"."category" ASC\
                """);
    }

    @Test
    void groupByWithWhereHavingOrderByFetch() {
        String result = dsl.select("customer_id", "COUNT(*)", "SUM(amount)", "AVG(quantity)")
                .from("orders")
                .as("o")
                .where("order_date")
                .between(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31))
                .groupBy("customer_id")
                .having("customer_id")
                .gt(1000)
                .andHaving("customer_id")
                .lt(9999)
                .orderByDesc("customer_id")
                .fetch(10)
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                SELECT "o"."customer_id", "o"."COUNT(*)", "o"."SUM(amount)", "o"."AVG(quantity)" \
                FROM "orders" AS o \
                WHERE ("o"."order_date" >= '2023-01-01') AND ("o"."order_date" <= '2023-12-31') \
                GROUP BY "o"."customer_id" \
                HAVING ("o"."customer_id" > 1000) AND ("o"."customer_id" < 9999) \
                ORDER BY "o"."customer_id" DESC \
                OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY\
                """);
    }
}
