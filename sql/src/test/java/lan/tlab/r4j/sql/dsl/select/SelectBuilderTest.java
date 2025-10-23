package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.LogicalCombinator;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderTest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void ok() {
        String result = dsl.select("name", "email").from("users").build();
        assertThat(result)
                .isEqualTo("""
            SELECT "users"."name", "users"."email" FROM "users"\
            """);
    }

    @Test
    void star() {
        String result = dsl.select("*").from("products").build();
        assertThat(result).isEqualTo("""
            SELECT * FROM "products"\
            """);
    }

    @Test
    void fromWithAlias() {
        String result = dsl.select("name", "email")
                .from("users")
                .as("u")
                .where("name")
                .eq("John")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                        SELECT "u"."name", "u"."email" FROM "users" AS u WHERE "u"."name" = 'John'\
                        """);
    }

    @Test
    void where() {
        String result = dsl.select("*").from("users").where("age").gt(18).build();
        assertThat(result).isEqualTo("""
            SELECT * FROM "users" WHERE "users"."age" > 18\
            """);
    }

    @Test
    void and() {
        String result = dsl.select("name", "email")
                .from("users")
                .where("age")
                .gt(18)
                .and("status")
                .eq("active")
                .and("country")
                .eq("Italy")
                .build();

        assertThat(result)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."email" FROM "users" WHERE ((\"users\".\"age\" > 18) AND (\"users\".\"status\" = 'active')) AND (\"users\".\"country\" = 'Italy')\
                        """);
    }

    @Test
    void or() {
        String sql = dsl.select("*")
                .from("users")
                .where("role")
                .eq("admin")
                .or("role")
                .eq("moderator")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" WHERE ("users"."role" = 'admin') OR ("users"."role" = 'moderator')\
                        """);
    }

    @Test
    void andOr() {
        String sql = dsl.select("*")
                .from("users")
                .where("status")
                .eq("active")
                .and("age")
                .gt(18)
                .or("role")
                .eq("admin")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" WHERE ((\"users\".\"status\" = 'active') AND (\"users\".\"age\" > 18)) OR (\"users\".\"role\" = 'admin')\
                        """);
    }

    @Test
    void orderBy() {
        String sql = dsl.select("name", "age").from("users").orderBy("name").build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."age" FROM "users" ORDER BY "users"."name" ASC\
                        """);
    }

    @Test
    void orderByDesc() {
        String sql = dsl.select("*").from("products").orderByDesc("created_at").build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "products" ORDER BY "products"."created_at" DESC\
            """);
    }

    @Test
    void fetch() {
        String sql = dsl.select("*").from("users").fetch(10).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void fetchWithOffset() {
        String sql = dsl.select("*").from("users").fetch(10).offset(20).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void offsetBeforeFetch() {
        String sql = dsl.select("*").from("users").offset(15).fetch(5).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 15 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void offsetZero() {
        String sql = dsl.select("*").from("users").fetch(5).offset(0).build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void offsetOverridesOnMultipleCalls() {
        String sql =
                dsl.select("*").from("users").offset(10).offset(20).fetch(5).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 5 ROWS ONLY\
            """);
    }

    @Test
    void offsetPreservedAcrossFetchChanges() {
        String sql = dsl.select("*").from("users").fetch(10).offset(25).fetch(8).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 25 ROWS FETCH NEXT 8 ROWS ONLY\
            """);
    }

    @Test
    void offsetPrecisionMaintained() {
        String sql = dsl.select("*").from("users").offset(23).fetch(10).build();

        assertThat(sql)
                .isEqualTo(
                        """
            SELECT * FROM "users" OFFSET 23 ROWS FETCH NEXT 10 ROWS ONLY\
            """);
    }

    @Test
    void fullSelectQuery() {
        String sql = dsl.select("name", "email", "age")
                .from("users")
                .where("status")
                .eq("active")
                .and("age")
                .gte(18)
                .and("country")
                .eq("Italy")
                .or("role")
                .eq("admin")
                .orderByDesc("created_at")
                .fetch(50)
                .offset(100)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT "users"."name", "users"."email", "users"."age" FROM "users" WHERE (((\"users\".\"status\" = 'active') AND (\"users\".\"age\" >= 18)) AND (\"users\".\"country\" = 'Italy')) OR (\"users\".\"role\" = 'admin') ORDER BY "users"."created_at" DESC OFFSET 100 ROWS FETCH NEXT 50 ROWS ONLY\
                        """);
    }

    @Test
    void isNull() {
        String sql = dsl.select("*").from("users").where("deleted_at").isNull().build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."deleted_at" IS NULL\
            """);
    }

    @Test
    void isNotNull() {
        String sql = dsl.select("*").from("users").where("email").isNotNull().build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."email" IS NOT NULL\
            """);
    }

    @Test
    void like() {
        String sql = dsl.select("*").from("users").where("name").like("%john%").build();

        assertThat(sql)
                .isEqualTo("""
            SELECT * FROM "users" WHERE "users"."name" LIKE '%john%'\
            """);
    }

    @Test
    void allComparisonOperators() {
        String sql = dsl.select("*")
                .from("products")
                .where("price")
                .gt(100)
                .and("discount")
                .lt(50)
                .and("rating")
                .gte(4)
                .and("stock")
                .lte(10)
                .and("category")
                .ne("deprecated")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "products" WHERE ((((\"products\".\"price\" > 100) AND (\"products\".\"discount\" < 50)) AND (\"products\".\"rating\" >= 4)) AND (\"products\".\"stock\" <= 10)) AND (\"products\".\"category\" != 'deprecated')\
                        """);
    }

    @Test
    void fromNotSpecified() {
        assertThatThrownBy(() -> dsl.select("*").build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("FROM table must be specified");
    }

    @Test
    void invalidTableName() {
        assertThatThrownBy(() -> dsl.select("*").from(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TableIdentifier name cannot be null or empty");
    }

    @Test
    void invalidColumnName() {
        assertThatThrownBy(() -> dsl.select("*").from("users").where(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void invalidAlias() {
        assertThatThrownBy(() -> dsl.select("*").from("users").as(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty");
    }

    @Test
    void invalidFetch() {
        assertThatThrownBy(() -> dsl.select("*").from("users").fetch(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Fetch rows must be positive, got: -1");
    }

    @Test
    void invalidOffset() {
        assertThatThrownBy(() -> dsl.select("*").from("users").offset(-5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Offset must be non-negative, got: -5");
    }

    // Tests for static helper methods
    @Test
    void hasValidConditionReturnsTrueForValidComparison() {
        Where whereWithComparison = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        assertThat(SelectBuilder.hasValidCondition(whereWithComparison)).isTrue();
    }

    @Test
    void hasValidConditionReturnsFalseForNullPredicate() {
        Where whereWithNull = Where.of(new NullPredicate());

        assertThat(SelectBuilder.hasValidCondition(whereWithNull)).isFalse();
    }

    @Test
    void combineWithExistingCreatesAndCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.getCondition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineWithExistingCreatesOrCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.getCondition()).isInstanceOf(AndOr.class);
        AndOr andOr = (AndOr) result.getCondition();
        assertThat(andOr.getOperator()).isEqualTo(lan.tlab.r4j.sql.ast.predicate.logical.LogicalOperator.OR);
    }

    @Test
    void combineConditionsWithNullWhereCreatesNewCondition() {
        Predicate condition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = SelectBuilder.combineConditions(null, condition, LogicalCombinator.AND);

        assertThat(result.getCondition()).isEqualTo(condition);
    }

    @Test
    void combineConditionsWithValidWhereCreatesCombinedCondition() {
        Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

        Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

        Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.OR);

        assertThat(result.getCondition()).isInstanceOf(AndOr.class);
    }

    @Test
    void combineConditionsWithNullPredicateCreatesNewCondition() {
        Where existingWhere = Where.of(new NullPredicate());

        Predicate newCondition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

        Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.AND);

        assertThat(result.getCondition()).isEqualTo(newCondition);
    }

    @Test
    void fromSubquery() {
        SelectBuilder subquery =
                dsl.select("id", "name").from("users").where("age").gt(18);

        String sql = dsl.select("*").from(subquery, "u").build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM (SELECT "users"."id", "users"."name" FROM "users" WHERE "users"."age" > 18) AS u\
                        """);
    }

    @Test
    void fromSubqueryWithWhere() {
        SelectBuilder subquery =
                dsl.select("id", "total").from("orders").where("status").eq("completed");

        String sql = dsl.select("*").from(subquery, "o").where("total").gt(100).build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM (SELECT "orders"."id", "orders"."total" FROM "orders" WHERE "orders"."status" = 'completed') AS o WHERE "o"."total" > 100\
                        """);
    }

    @Test
    void whereWithScalarSubquery() {
        SelectBuilder subquery = dsl.select("*").from("users").where("age").gt(50);

        String sql =
                dsl.select("name").from("employees").where("age").gt(subquery).build();

        assertThat(sql)
                .contains("WHERE \"employees\".\"age\" > (SELECT * FROM \"users\" WHERE \"users\".\"age\" > 50)");
    }

    @Test
    void whereWithScalarSubqueryEquals() {
        SelectBuilder maxAgeSubquery = dsl.select("*").from("users");

        String sql = dsl.select("*")
                .from("employees")
                .where("age")
                .eq(maxAgeSubquery)
                .build();

        assertThat(sql).contains("WHERE \"employees\".\"age\" = (SELECT * FROM \"users\")");
    }

    @Test
    void fromSubqueryNullSubquery() {
        assertThatThrownBy(() -> dsl.select("*").from((SelectBuilder) null, "alias"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Subquery cannot be null");
    }

    @Test
    void fromSubqueryNullAlias() {
        SelectBuilder subquery = dsl.select("*").from("users");
        assertThatThrownBy(() -> dsl.select("*").from(subquery, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty for subquery");
    }

    @Test
    void fromSubqueryEmptyAlias() {
        SelectBuilder subquery = dsl.select("*").from("users");
        assertThatThrownBy(() -> dsl.select("*").from(subquery, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Alias cannot be null or empty for subquery");
    }

    @Test
    void havingWithSingleCondition() {
        String sql = dsl.select("*")
                .from("users")
                .groupBy("age")
                .having("age")
                .gt(18)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" GROUP BY "users"."age" HAVING "users"."age" > 18\
                        """);
    }

    @Test
    void havingWithAndCondition() {
        String sql = dsl.select("*")
                .from("users")
                .groupBy("age")
                .having("age")
                .gt(18)
                .andHaving("age")
                .lt(65)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" GROUP BY "users"."age" HAVING ("users"."age" > 18) AND ("users"."age" < 65)\
                        """);
    }

    @Test
    void havingWithOrCondition() {
        String sql = dsl.select("*")
                .from("users")
                .groupBy("status")
                .having("status")
                .eq("active")
                .orHaving("status")
                .eq("pending")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "users" GROUP BY "users"."status" HAVING ("users"."status" = 'active') OR ("users"."status" = 'pending')\
                        """);
    }

    @Test
    void havingWithComplexConditions() {
        String sql = dsl.select("*")
                .from("orders")
                .groupBy("customer_id")
                .having("customer_id")
                .gt(100)
                .andHaving("customer_id")
                .lt(500)
                .orHaving("customer_id")
                .eq(999)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "orders" GROUP BY "orders"."customer_id" HAVING (("orders"."customer_id" > 100) AND ("orders"."customer_id" < 500)) OR ("orders"."customer_id" = 999)\
                        """);
    }

    @Test
    void havingWithWhereAndOrderBy() {
        String sql = dsl.select("*")
                .from("products")
                .where("category")
                .eq("electronics")
                .groupBy("brand")
                .having("brand")
                .like("%Apple%")
                .orderBy("brand")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        SELECT * FROM "products" WHERE "products"."category" = 'electronics' GROUP BY "products"."brand" HAVING "products"."brand" LIKE '%Apple%' ORDER BY "products"."brand" ASC\
                        """);
    }

    @Test
    void invalidHavingColumn() {
        assertThatThrownBy(() -> dsl.select("*").from("users").groupBy("age").having(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void countStar() {
        String result = dsl.select().countStar().from("users").build();
        assertThat(result).isEqualTo("""
            SELECT COUNT(*) FROM "users"\
            """);
    }

    @Test
    void countStarWithAlias() {
        String result = dsl.select().countStar().as("total").from("users").build();
        assertThat(result).isEqualTo("""
            SELECT COUNT(*) AS total FROM "users"\
            """);
    }

    @Test
    void sum() {
        String result = dsl.select().sum("amount").from("orders").build();
        assertThat(result).isEqualTo("""
            SELECT SUM("orders"."amount") FROM "orders"\
            """);
    }

    @Test
    void sumWithAlias() {
        String result =
                dsl.select().sum("amount").as("total_amount").from("orders").build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT SUM("orders"."amount") AS total_amount FROM "orders"\
            """);
    }

    @Test
    void avg() {
        String result = dsl.select().avg("score").from("students").build();
        assertThat(result).isEqualTo("""
            SELECT AVG("students"."score") FROM "students"\
            """);
    }

    @Test
    void avgWithAlias() {
        String result =
                dsl.select().avg("score").as("avg_score").from("students").build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT AVG("students"."score") AS avg_score FROM "students"\
            """);
    }

    @Test
    void count() {
        String result = dsl.select().count("id").from("users").build();
        assertThat(result).isEqualTo("""
            SELECT COUNT("users"."id") FROM "users"\
            """);
    }

    @Test
    void countWithAlias() {
        String result = dsl.select().count("id").as("user_count").from("users").build();
        assertThat(result)
                .isEqualTo("""
            SELECT COUNT("users"."id") AS user_count FROM "users"\
            """);
    }

    @Test
    void countDistinct() {
        String result = dsl.select().countDistinct("email").from("users").build();
        assertThat(result)
                .isEqualTo("""
            SELECT COUNT(DISTINCT "users"."email") FROM "users"\
            """);
    }

    @Test
    void countDistinctWithAlias() {
        String result = dsl.select()
                .countDistinct("email")
                .as("unique_emails")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT COUNT(DISTINCT "users"."email") AS unique_emails FROM "users"\
            """);
    }

    @Test
    void max() {
        String result = dsl.select().max("price").from("products").build();
        assertThat(result).isEqualTo("""
            SELECT MAX("products"."price") FROM "products"\
            """);
    }

    @Test
    void maxWithAlias() {
        String result =
                dsl.select().max("price").as("max_price").from("products").build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT MAX("products"."price") AS max_price FROM "products"\
            """);
    }

    @Test
    void min() {
        String result = dsl.select().min("price").from("products").build();
        assertThat(result).isEqualTo("""
            SELECT MIN("products"."price") FROM "products"\
            """);
    }

    @Test
    void minWithAlias() {
        String result =
                dsl.select().min("price").as("min_price").from("products").build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT MIN("products"."price") AS min_price FROM "products"\
            """);
    }

    @Test
    void sumWithGroupBy() {
        String result =
                dsl.select().sum("amount").from("orders").groupBy("customer_id").build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT SUM("orders"."amount") FROM "orders" GROUP BY "orders"."customer_id"\
            """);
    }

    @Test
    void countWithWhere() {
        String result =
                dsl.select().countStar().from("users").where("active").eq(true).build();
        assertThat(result)
                .isEqualTo("""
            SELECT COUNT(*) FROM "users" WHERE "users"."active" = true\
            """);
    }

    @Test
    void avgWithGroupByAndHaving() {
        String result = dsl.select()
                .avg("salary")
                .from("employees")
                .groupBy("department")
                .having("department")
                .ne("HR")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
                        SELECT AVG("employees"."salary") FROM "employees" GROUP BY "employees"."department" HAVING "employees"."department" != 'HR'\
                        """);
    }

    @Test
    void multipleAggregatesWithoutAliases() {
        String result = dsl.select().sum("score").max("createdAt").from("users").build();
        assertThat(result)
                .isEqualTo(
                        """
                        SELECT SUM("users"."score"), MAX("users"."createdAt") FROM "users"\
                        """);
    }

    @Test
    void multipleAggregatesWithAliases() {
        String result = dsl.select()
                .sum("score")
                .as("total_score")
                .max("createdAt")
                .as("latest")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
                        SELECT SUM("users"."score") AS total_score, MAX("users"."createdAt") AS latest FROM "users"\
                        """);
    }

    @Test
    void multipleAggregatesWithOneAlias() {
        String result = dsl.select()
                .sum("score")
                .max("createdAt")
                .as("latest")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
                        SELECT SUM("users"."score"), MAX("users"."createdAt") AS latest FROM "users"\
                        """);
    }

    @Test
    void column() {
        String result = dsl.select().column("name").from("users").build();
        assertThat(result).isEqualTo("""
            SELECT "users"."name" FROM "users"\
            """);
    }

    @Test
    void columnWithAlias() {
        String result =
                dsl.select().column("name").as("user_name").from("users").build();
        assertThat(result).isEqualTo("""
            SELECT "users"."name" AS user_name FROM "users"\
            """);
    }

    @Test
    void multipleColumns() {
        String result =
                dsl.select().column("name").column("email").from("users").build();
        assertThat(result)
                .isEqualTo("""
            SELECT "users"."name", "users"."email" FROM "users"\
            """);
    }

    @Test
    void multipleColumnsWithAliases() {
        String result = dsl.select()
                .column("name")
                .as("user_name")
                .column("email")
                .as("user_email")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT "users"."name" AS user_name, "users"."email" AS user_email FROM "users"\
            """);
    }

    @Test
    void multipleColumnsWithOneAlias() {
        String result = dsl.select()
                .column("name")
                .column("email")
                .as("user_email")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT "users"."name", "users"."email" AS user_email FROM "users"\
            """);
    }

    @Test
    void tableQualifiedColumn() {
        String result = dsl.select().column("users", "name").from("users").build();
        assertThat(result).isEqualTo("""
            SELECT "users"."name" FROM "users"\
            """);
    }

    @Test
    void mixedColumnsAndAggregates() {
        String result = dsl.select()
                .column("name")
                .sum("score")
                .as("total_score")
                .from("users")
                .build();
        assertThat(result)
                .isEqualTo(
                        """
            SELECT "users"."name", SUM("users"."score") AS total_score FROM "users"\
            """);
    }
}
