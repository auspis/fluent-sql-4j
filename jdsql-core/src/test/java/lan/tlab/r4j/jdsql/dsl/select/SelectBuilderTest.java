package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SelectBuilderTest {

    private DialectRenderer renderer;
    private Connection connection;

    @BeforeEach
    void setUp() {
        renderer = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    @Test
    void ok() {
        PreparedStatement result =
                new SelectBuilder(renderer, "name", "email").from("users").buildPreparedStatement(connection);
        assertThat(result)
                .isEqualTo("""
            SELECT "users"."name", "users"."email" FROM "users"\
            """);
    }

    //     @Test
    //     void star() {
    //         PreparedStatement result = new SelectBuilder(renderer,
    // "*").from("products").buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT * FROM "products"\
    //             """);
    //     }

    //     @Test
    //     void fromWithAlias() {
    //         PreparedStatement result = new SelectBuilder(renderer, "name", "email")
    //                 .from("users")
    //                 .as("u")
    //                 .where()
    //                 .column("name")
    //                 .eq("John")
    //                 .buildPreparedStatement(connection);

    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT "u"."name", "u"."email" FROM "users" AS u WHERE "u"."name" = 'John'\
    //                         """);
    //     }

    //     @Test
    //     void where() {
    //         PreparedStatement result = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .where()
    //                 .column("age")
    //                 .gt(18)
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT * FROM "users" WHERE "users"."age" > 18\
    //             """);
    //     }

    //     @Test
    //     void and() {
    //         PreparedStatement result = new SelectBuilder(renderer, "name", "email")
    //                 .from("users")
    //                 .where()
    //                 .column("age")
    //                 .gt(18)
    //                 .and()
    //                 .column("status")
    //                 .eq("active")
    //                 .and()
    //                 .column("country")
    //                 .eq("Italy")
    //                 .buildPreparedStatement(connection);

    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT "users"."name", "users"."email" FROM "users" WHERE ((\"users\".\"age\" > 18) AND
    // (\"users\".\"status\" = 'active')) AND (\"users\".\"country\" = 'Italy')\
    //                         """);
    //     }

    //     @Test
    //     void or() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .where()
    //                 .column("role")
    //                 .eq("admin")
    //                 .or()
    //                 .column("role")
    //                 .eq("moderator")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "users" WHERE ("users"."role" = 'admin') OR ("users"."role" = 'moderator')\
    //                         """);
    //     }

    //     @Test
    //     void andOr() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .where()
    //                 .column("status")
    //                 .eq("active")
    //                 .and()
    //                 .column("age")
    //                 .gt(18)
    //                 .or()
    //                 .column("role")
    //                 .eq("admin")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "users" WHERE ((\"users\".\"status\" = 'active') AND (\"users\".\"age\" >
    // 18)) OR (\"users\".\"role\" = 'admin')\
    //                         """);
    //     }

    //     @Test
    //     void orderBy() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "name", "age")
    //                 .from("users")
    //                 .orderBy("name")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT "users"."name", "users"."age" FROM "users" ORDER BY "users"."name" ASC\
    //                         """);
    //     }

    //     @Test
    //     void orderByDesc() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .orderByDesc("created_at")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "products" ORDER BY "products"."created_at" DESC\
    //             """);
    //     }

    //     @Test
    //     void fetch() {
    //         PreparedStatement sql = new SelectBuilder(renderer,
    // "*").from("users").fetch(10).buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void fetchWithOffset() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .fetch(10)
    //                 .offset(20)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 10 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void offsetBeforeFetch() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .offset(15)
    //                 .fetch(5)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 15 ROWS FETCH NEXT 5 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void offsetZero() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .fetch(5)
    //                 .offset(0)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo("""
    //             SELECT * FROM "users" OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void offsetOverridesOnMultipleCalls() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .offset(10)
    //                 .offset(20)
    //                 .fetch(5)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 20 ROWS FETCH NEXT 5 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void offsetPreservedAcrossFetchChanges() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .fetch(10)
    //                 .offset(25)
    //                 .fetch(8)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 25 ROWS FETCH NEXT 8 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void offsetPrecisionMaintained() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .offset(23)
    //                 .fetch(10)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //             SELECT * FROM "users" OFFSET 23 ROWS FETCH NEXT 10 ROWS ONLY\
    //             """);
    //     }

    //     @Test
    //     void fullSelectQuery() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "name", "email", "age")
    //                 .from("users")
    //                 .where()
    //                 .column("status")
    //                 .eq("active")
    //                 .and()
    //                 .column("age")
    //                 .gte(18)
    //                 .and()
    //                 .column("country")
    //                 .eq("Italy")
    //                 .or()
    //                 .column("role")
    //                 .eq("admin")
    //                 .orderByDesc("created_at")
    //                 .fetch(50)
    //                 .offset(100)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT "users"."name", "users"."email", "users"."age" FROM "users" WHERE
    // (((\"users\".\"status\" = 'active') AND (\"users\".\"age\" >= 18)) AND (\"users\".\"country\" = 'Italy')) OR
    // (\"users\".\"role\" = 'admin') ORDER BY "users"."created_at" DESC OFFSET 100 ROWS FETCH NEXT 50 ROWS ONLY\
    //                         """);
    //     }

    //     @Test
    //     void allComparisonOperators() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .where()
    //                 .column("price")
    //                 .gt(100)
    //                 .and()
    //                 .column("discount")
    //                 .lt(50)
    //                 .and()
    //                 .column("rating")
    //                 .gte(4)
    //                 .and()
    //                 .column("stock")
    //                 .lte(10)
    //                 .and()
    //                 .column("category")
    //                 .ne("deprecated")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "products" WHERE ((((\"products\".\"price\" > 100) AND
    // (\"products\".\"discount\" < 50)) AND (\"products\".\"rating\" >= 4)) AND (\"products\".\"stock\" <= 10)) AND
    // (\"products\".\"category\" != 'deprecated')\
    //                         """);
    //     }

    //     @Test
    //     void fromNotSpecified() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").build())
    //                 .isInstanceOf(IllegalStateException.class)
    //                 .hasMessage("FROM table must be specified");
    //     }

    //     @Test
    //     void invalidTableName() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from(""))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("TableIdentifier name cannot be null or empty");
    //     }

    //     @Test
    //     void invalidColumnName() {
    //         assertThatThrownBy(() ->
    //                         new SelectBuilder(renderer, "*").from("users").where().column(""))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Column name cannot be null or empty");
    //     }

    //     @Test
    //     void invalidAlias() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("users").as(""))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Alias cannot be null or empty");
    //     }

    //     @Test
    //     void invalidFetch() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("users").fetch(-1))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Fetch rows must be positive, got: -1");
    //     }

    //     @Test
    //     void invalidOffset() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from("users").offset(-5))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Offset must be non-negative, got: -5");
    //     }

    //     // Tests for static helper methods
    //     @Test
    //     void hasValidConditionReturnsTrueForValidComparison() {
    //         Where whereWithComparison = Where.of(Comparison.eq(ColumnReference.of("users", "name"),
    // Literal.of("John")));

    //         assertThat(SelectBuilder.hasValidCondition(whereWithComparison)).isTrue();
    //     }

    //     @Test
    //     void hasValidConditionReturnsFalseForNullPredicate() {
    //         Where whereWithNull = Where.of(new NullPredicate());

    //         assertThat(SelectBuilder.hasValidCondition(whereWithNull)).isFalse();
    //     }

    //     @Test
    //     void combineWithExistingCreatesAndCondition() {
    //         Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

    //         Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

    //         Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.AND);

    //         assertThat(result.condition()).isInstanceOf(AndOr.class);
    //     }

    //     @Test
    //     void combineWithExistingCreatesOrCondition() {
    //         Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

    //         Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

    //         Where result = SelectBuilder.combineWithExisting(existingWhere, newCondition, LogicalCombinator.OR);

    //         assertThat(result.condition()).isInstanceOf(AndOr.class);
    //         AndOr andOr = (AndOr) result.condition();
    //
    // assertThat(andOr.operator()).isEqualTo(lan.tlab.r4j.jdsql.ast.common.predicate.logical.LogicalOperator.OR);
    //     }

    //     @Test
    //     void combineConditionsWithNullWhereCreatesNewCondition() {
    //         Predicate condition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

    //         Where result = SelectBuilder.combineConditions(null, condition, LogicalCombinator.AND);

    //         assertThat(result.condition()).isEqualTo(condition);
    //     }

    //     @Test
    //     void combineConditionsWithValidWhereCreatesCombinedCondition() {
    //         Where existingWhere = Where.of(Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John")));

    //         Predicate newCondition = Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25));

    //         Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.OR);

    //         assertThat(result.condition()).isInstanceOf(AndOr.class);
    //     }

    //     @Test
    //     void combineConditionsWithNullPredicateCreatesNewCondition() {
    //         Where existingWhere = Where.of(new NullPredicate());

    //         Predicate newCondition = Comparison.eq(ColumnReference.of("users", "name"), Literal.of("John"));

    //         Where result = SelectBuilder.combineConditions(existingWhere, newCondition, LogicalCombinator.AND);

    //         assertThat(result.condition()).isEqualTo(newCondition);
    //     }

    //     @Test
    //     void fromSubquery() {
    //         SelectBuilder subquery = new SelectBuilder(renderer, "id", "name")
    //                 .from("users")
    //                 .where()
    //                 .column("age")
    //                 .gt(18);

    //         PreparedStatement sql = new SelectBuilder(renderer, "*").from(subquery,
    // "u").buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM (SELECT "users"."id", "users"."name" FROM "users" WHERE "users"."age" > 18)
    // AS u\
    //                         """);
    //     }

    //     @Test
    //     void fromSubqueryWithWhere() {
    //         SelectBuilder subquery = new SelectBuilder(renderer, "id", "total")
    //                 .from("orders")
    //                 .where()
    //                 .column("status")
    //                 .eq("completed");

    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from(subquery, "o")
    //                 .where()
    //                 .column("total")
    //                 .gt(100)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM (SELECT "orders"."id", "orders"."total" FROM "orders" WHERE
    // "orders"."status" = 'completed') AS o WHERE "o"."total" > 100\
    //                         """);
    //     }

    //     @Test
    //     void whereWithScalarSubquery() {
    //         SelectBuilder subquery = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .where()
    //                 .column("age")
    //                 .gt(50);

    //         PreparedStatement sql = new SelectBuilder(renderer, "name")
    //                 .from("employees")
    //                 .where()
    //                 .column("age")
    //                 .gt(subquery)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .contains("WHERE \"employees\".\"age\" > (SELECT * FROM \"users\" WHERE \"users\".\"age\" >
    // 50)");
    //     }

    //     @Test
    //     void whereWithScalarSubqueryEquals() {
    //         SelectBuilder maxAgeSubquery = new SelectBuilder(renderer, "*").from("users");

    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("employees")
    //                 .where()
    //                 .column("age")
    //                 .eq(maxAgeSubquery)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("WHERE \"employees\".\"age\" = (SELECT * FROM \"users\")");
    //     }

    //     @Test
    //     void fromSubqueryNullSubquery() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from((SelectBuilder) null, "alias"))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Subquery cannot be null");
    //     }

    //     @Test
    //     void fromSubqueryNullAlias() {
    //         SelectBuilder subquery = new SelectBuilder(renderer, "*").from("users");
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from(subquery, null))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Alias cannot be null or empty for subquery");
    //     }

    //     @Test
    //     void fromSubqueryEmptyAlias() {
    //         SelectBuilder subquery = new SelectBuilder(renderer, "*").from("users");
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*").from(subquery, ""))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Alias cannot be null or empty for subquery");
    //     }

    //     @Test
    //     void havingWithSingleCondition() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("age")
    //                 .having("age")
    //                 .gt(18)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "users" GROUP BY "users"."age" HAVING "users"."age" > 18\
    //                         """);
    //     }

    //     @Test
    //     void havingWithAndCondition() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("age")
    //                 .having("age")
    //                 .gt(18)
    //                 .andHaving("age")
    //                 .lt(65)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "users" GROUP BY "users"."age" HAVING ("users"."age" > 18) AND
    // ("users"."age" < 65)\
    //                         """);
    //     }

    //     @Test
    //     void havingWithOrCondition() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("status")
    //                 .having("status")
    //                 .eq("active")
    //                 .orHaving("status")
    //                 .eq("pending")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "users" GROUP BY "users"."status" HAVING ("users"."status" = 'active') OR
    // ("users"."status" = 'pending')\
    //                         """);
    //     }

    //     @Test
    //     void havingWithComplexConditions() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("customer_id")
    //                 .having("customer_id")
    //                 .gt(100)
    //                 .andHaving("customer_id")
    //                 .lt(500)
    //                 .orHaving("customer_id")
    //                 .eq(999)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "orders" GROUP BY "orders"."customer_id" HAVING (("orders"."customer_id" >
    // 100) AND ("orders"."customer_id" < 500)) OR ("orders"."customer_id" = 999)\
    //                         """);
    //     }

    //     @Test
    //     void havingWithWhereAndOrderBy() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .where()
    //                 .column("category")
    //                 .eq("electronics")
    //                 .groupBy("brand")
    //                 .having("brand")
    //                 .like("%Apple%")
    //                 .orderBy("brand")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .isEqualTo(
    //                         """
    //                         SELECT * FROM "products" WHERE "products"."category" = 'electronics' GROUP BY
    // "products"."brand" HAVING "products"."brand" LIKE '%Apple%' ORDER BY "products"."brand" ASC\
    //                         """);
    //     }

    //     @Test
    //     void invalidHavingColumn() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
    //                         .from("users")
    //                         .groupBy("age")
    //                         .having(""))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Column name cannot be null or empty");
    //     }

    //     // Additional HAVING tests for better HavingConditionBuilder coverage

    //     @Test
    //     void havingStringComparisons() {
    //         // Test all string comparison operators
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .groupBy("category")
    //                 .having("category")
    //                 .eq("electronics")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"products\".\"category\" = 'electronics'");

    //         sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .groupBy("category")
    //                 .having("category")
    //                 .ne("books")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"products\".\"category\" != 'books'");
    //     }

    //     @Test
    //     void havingNumberComparisons() {
    //         // Test additional number comparison operators
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("total")
    //                 .having("total")
    //                 .gte(100.0)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"orders\".\"total\" >= 100.0");

    //         sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("total")
    //                 .having("total")
    //                 .lte(500)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"orders\".\"total\" <= 500");
    //     }

    //     @Test
    //     void havingBooleanComparisons() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("active")
    //                 .having("active")
    //                 .eq(true)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"users\".\"active\" = true");

    //         sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("active")
    //                 .having("active")
    //                 .ne(false)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"users\".\"active\" != false");
    //     }

    //     @Test
    //     void havingNullChecks() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("email")
    //                 .having("email")
    //                 .isNull()
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"users\".\"email\" IS NULL");

    //         sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("email")
    //                 .having("email")
    //                 .isNotNull()
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"users\".\"email\" IS NOT NULL");
    //     }

    //     @Test
    //     void havingBetweenNumbers() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .groupBy("price")
    //                 .having("price")
    //                 .between(10.0, 100.0)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING (\"products\".\"price\" >= 10.0) AND (\"products\".\"price\" <=
    // 100.0)");
    //     }

    //     @Test
    //     void havingBetweenDates() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("order_date")
    //                 .having("order_date")
    //                 .between(java.time.LocalDate.of(2023, 1, 1), java.time.LocalDate.of(2023, 12, 31))
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .contains(
    //                         "HAVING (\"orders\".\"order_date\" >= '2023-01-01') AND (\"orders\".\"order_date\" <=
    // '2023-12-31')");
    //     }

    //     @Test
    //     void havingSubqueryComparison() {
    //         // Create a simple subquery without aggregate functions
    //         SelectBuilder subquery = new SelectBuilder(renderer, "budget")
    //                 .from("departments")
    //                 .where()
    //                 .column("active")
    //                 .eq(true);

    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("departments")
    //                 .groupBy("budget")
    //                 .having("budget")
    //                 .gt(subquery)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .contains(
    //                         "HAVING \"departments\".\"budget\" > (SELECT \"departments\".\"budget\" FROM
    // \"departments\" WHERE \"departments\".\"active\" = true)");
    //     }

    //     @Test
    //     void havingWithNullSubquery_throwsException() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
    //                         .from("users")
    //                         .groupBy("age")
    //                         .having("age")
    //                         .eq((SelectBuilder) null))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("Subquery cannot be null");
    //     }

    //     @Test
    //     void havingInOperatorWithStrings() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .groupBy("category")
    //                 .having("category")
    //                 .in("electronics", "books", "toys")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"products\".\"category\" IN('electronics', 'books', 'toys')");
    //     }

    //     @Test
    //     void havingInOperatorWithNumbers() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("customer_id")
    //                 .having("customer_id")
    //                 .in(100, 200, 300)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"orders\".\"customer_id\" IN(100, 200, 300)");
    //     }

    //     @Test
    //     void havingInOperatorWithBooleans() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("users")
    //                 .groupBy("active")
    //                 .having("active")
    //                 .in(true)
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"users\".\"active\" IN(true)");
    //     }

    //     @Test
    //     void havingInOperatorWithDates() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("events")
    //                 .groupBy("event_date")
    //                 .having("event_date")
    //                 .in(java.time.LocalDate.of(2023, 1, 1), java.time.LocalDate.of(2023, 12, 31))
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql).contains("HAVING \"events\".\"event_date\" IN('2023-01-01', '2023-12-31')");
    //     }

    //     @Test
    //     void havingInOperatorWithAndCondition() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("products")
    //                 .groupBy("category", "brand")
    //                 .having("category")
    //                 .in("electronics", "computers")
    //                 .andHaving("brand")
    //                 .ne("unknown")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .contains(
    //                         "HAVING (\"products\".\"category\" IN('electronics', 'computers')) AND
    // (\"products\".\"brand\" != 'unknown')");
    //     }

    //     @Test
    //     void havingInOperatorWithOrCondition() {
    //         PreparedStatement sql = new SelectBuilder(renderer, "*")
    //                 .from("orders")
    //                 .groupBy("status")
    //                 .having("status")
    //                 .in("completed", "shipped")
    //                 .orHaving("status")
    //                 .eq("delivered")
    //                 .buildPreparedStatement(connection);

    //         assertThat(sql)
    //                 .contains(
    //                         "HAVING (\"orders\".\"status\" IN('completed', 'shipped')) OR (\"orders\".\"status\" =
    // 'delivered')");
    //     }

    //     @Test
    //     void havingInOperatorEmptyValues_throwsException() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
    //                         .from("users")
    //                         .groupBy("age")
    //                         .having("age")
    //                         .in(new String[0]))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("At least one value must be provided for IN clause");
    //     }

    //     @Test
    //     void havingInOperatorNullValues_throwsException() {
    //         assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
    //                         .from("users")
    //                         .groupBy("age")
    //                         .having("age")
    //                         .in((String[]) null))
    //                 .isInstanceOf(IllegalArgumentException.class)
    //                 .hasMessage("At least one value must be provided for IN clause");
    //     }

    //     @Test
    //     void countStar() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .countStar()
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT COUNT(*) FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void countStarWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .countStar()
    //                 .as("total")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT COUNT(*) AS total FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void sum() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("amount")
    //                 .from("orders")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT SUM("orders"."amount") FROM "orders"\
    //             """);
    //     }

    //     @Test
    //     void sumWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("amount")
    //                 .as("total_amount")
    //                 .from("orders")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT SUM("orders"."amount") AS total_amount FROM "orders"\
    //             """);
    //     }

    //     @Test
    //     void avg() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .avg("score")
    //                 .from("students")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT AVG("students"."score") FROM "students"\
    //             """);
    //     }

    //     @Test
    //     void avgWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .avg("score")
    //                 .as("avg_score")
    //                 .from("students")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT AVG("students"."score") AS avg_score FROM "students"\
    //             """);
    //     }

    //     @Test
    //     void count() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .count("id")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT COUNT("users"."id") FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void countWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .count("id")
    //                 .as("user_count")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo("""
    //             SELECT COUNT("users"."id") AS user_count FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void countDistinct() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .countDistinct("email")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo("""
    //             SELECT COUNT(DISTINCT "users"."email") FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void countDistinctWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .countDistinct("email")
    //                 .as("unique_emails")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT COUNT(DISTINCT "users"."email") AS unique_emails FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void max() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .max("price")
    //                 .from("products")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT MAX("products"."price") FROM "products"\
    //             """);
    //     }

    //     @Test
    //     void maxWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .max("price")
    //                 .as("max_price")
    //                 .from("products")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT MAX("products"."price") AS max_price FROM "products"\
    //             """);
    //     }

    //     @Test
    //     void min() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .min("price")
    //                 .from("products")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT MIN("products"."price") FROM "products"\
    //             """);
    //     }

    //     @Test
    //     void minWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .min("price")
    //                 .as("min_price")
    //                 .from("products")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT MIN("products"."price") AS min_price FROM "products"\
    //             """);
    //     }

    //     @Test
    //     void sumWithGroupBy() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("amount")
    //                 .from("orders")
    //                 .groupBy("customer_id")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT SUM("orders"."amount") FROM "orders" GROUP BY "orders"."customer_id"\
    //             """);
    //     }

    //     @Test
    //     void countWithWhere() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .countStar()
    //                 .from("users")
    //                 .where()
    //                 .column("active")
    //                 .eq(true)
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo("""
    //             SELECT COUNT(*) FROM "users" WHERE "users"."active" = true\
    //             """);
    //     }

    //     @Test
    //     void avgWithGroupByAndHaving() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .avg("salary")
    //                 .from("employees")
    //                 .groupBy("department")
    //                 .having("department")
    //                 .ne("HR")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT AVG("employees"."salary") FROM "employees" GROUP BY "employees"."department"
    // HAVING "employees"."department" != 'HR'\
    //                         """);
    //     }

    //     @Test
    //     void multipleAggregatesWithoutAliases() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("score")
    //                 .max("createdAt")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT SUM("users"."score"), MAX("users"."createdAt") FROM "users"\
    //                         """);
    //     }

    //     @Test
    //     void multipleAggregatesWithAliases() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("score")
    //                 .as("total_score")
    //                 .max("createdAt")
    //                 .as("latest")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT SUM("users"."score") AS total_score, MAX("users"."createdAt") AS latest FROM
    // "users"\
    //                         """);
    //     }

    //     @Test
    //     void multipleAggregatesWithOneAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .sum("score")
    //                 .max("createdAt")
    //                 .as("latest")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //                         SELECT SUM("users"."score"), MAX("users"."createdAt") AS latest FROM "users"\
    //                         """);
    //     }

    //     @Test
    //     void column() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT "users"."name" FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void columnWithAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .as("user_name")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT "users"."name" AS user_name FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void multipleColumns() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .column("email")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo("""
    //             SELECT "users"."name", "users"."email" FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void multipleColumnsWithAliases() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .as("user_name")
    //                 .column("email")
    //                 .as("user_email")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT "users"."name" AS user_name, "users"."email" AS user_email FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void multipleColumnsWithOneAlias() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .column("email")
    //                 .as("user_email")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT "users"."name", "users"."email" AS user_email FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void tableQualifiedColumn() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("users", "name")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result).isEqualTo("""
    //             SELECT "users"."name" FROM "users"\
    //             """);
    //     }

    //     @Test
    //     void mixedColumnsAndAggregates() {
    //         String result = new SelectProjectionBuilder<>(renderer)
    //                 .column("name")
    //                 .sum("score")
    //                 .as("total_score")
    //                 .from("users")
    //                 .buildPreparedStatement(connection);
    //         assertThat(result)
    //                 .isEqualTo(
    //                         """
    //             SELECT "users"."name", SUM("users"."score") AS total_score FROM "users"\
    //             """);
    //     }
}
