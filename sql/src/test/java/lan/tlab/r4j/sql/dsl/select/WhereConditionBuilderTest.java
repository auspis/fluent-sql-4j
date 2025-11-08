package lan.tlab.r4j.sql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereConditionBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void stringComparisons() {
        // Test string equality
        String sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .eq("John")
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" = 'John'""");

        // Test string inequality
        sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .ne("Jane")
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" != 'Jane'""");

        // Test string comparisons
        sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .gt("A")
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" > 'A'""");

        sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .lt("Z")
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" < 'Z'""");

        sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .gte("B")
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" >= 'B'""");

        sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .lte("Y")
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" <= 'Y'""");
    }

    @Test
    void numberComparisons() {
        // Test integer operations
        String sql = new SelectBuilder(renderer, "age")
                .from("users")
                .where("age")
                .eq(25)
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."age" FROM "users" WHERE "users"."age" = 25""");

        sql = new SelectBuilder(renderer, "age")
                .from("users")
                .where("age")
                .ne(30)
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."age" FROM "users" WHERE "users"."age" != 30""");

        sql = new SelectBuilder(renderer, "age")
                .from("users")
                .where("age")
                .gt(18)
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."age" FROM "users" WHERE "users"."age" > 18""");

        sql = new SelectBuilder(renderer, "age")
                .from("users")
                .where("age")
                .lt(65)
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "users"."age" FROM "users" WHERE "users"."age" < 65""");

        // Test double operations
        sql = new SelectBuilder(renderer, "salary")
                .from("employees")
                .where("salary")
                .gte(50000.5)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "employees"."salary" FROM "employees" WHERE "employees"."salary" >= 50000.5""");

        sql = new SelectBuilder(renderer, "salary")
                .from("employees")
                .where("salary")
                .lte(100000.75)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "employees"."salary" FROM "employees" WHERE "employees"."salary" <= 100000.75""");
    }

    @Test
    void booleanComparisons() {
        // Test boolean equality
        String sql = new SelectBuilder(renderer, "active")
                .from("users")
                .where("active")
                .eq(true)
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."active" FROM "users" WHERE "users"."active" = true""");

        // Test boolean inequality
        sql = new SelectBuilder(renderer, "active")
                .from("users")
                .where("active")
                .ne(false)
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."active" FROM "users" WHERE "users"."active" != false""");
    }

    @Test
    void localDateComparisons() {
        LocalDate date = LocalDate.of(2024, 3, 15);

        // Test LocalDate operations
        String sql = new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where("birth_date")
                .eq(date)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."birth_date" FROM "users" WHERE "users"."birth_date" = '2024-03-15'""");

        sql = new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where("birth_date")
                .gt(date)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."birth_date" FROM "users" WHERE "users"."birth_date" > '2024-03-15'""");

        sql = new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where("birth_date")
                .lt(date)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."birth_date" FROM "users" WHERE "users"."birth_date" < '2024-03-15'""");
    }

    @Test
    void localDateTimeComparisons() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 30, 45);

        // Test LocalDateTime operations
        String sql = new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where("created_at")
                .eq(dateTime)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "posts"."created_at" FROM "posts" WHERE "posts"."created_at" = '2024-03-15T10:30:45'""");

        sql = new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where("created_at")
                .gte(dateTime)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "posts"."created_at" FROM "posts" WHERE "posts"."created_at" >= '2024-03-15T10:30:45'""");
    }

    @Test
    void likePatternMatching() {
        String sql = new SelectBuilder(renderer, "name")
                .from("users")
                .where("name")
                .like("John%")
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."name" FROM "users" WHERE "users"."name" LIKE 'John%'""");

        sql = new SelectBuilder(renderer, "email")
                .from("users")
                .where("email")
                .like("%@example.com")
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."email" FROM "users" WHERE "users"."email" LIKE '%@example.com'""");
    }

    @Test
    void nullChecks() {
        // Test IS NULL
        String sql = new SelectBuilder(renderer, "email")
                .from("users")
                .where("email")
                .isNull()
                .build();
        assertThat(sql)
                .isEqualTo("""
                SELECT "users"."email" FROM "users" WHERE "users"."email" IS NULL""");

        // Test IS NOT NULL
        sql = new SelectBuilder(renderer, "email")
                .from("users")
                .where("email")
                .isNotNull()
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."email" FROM "users" WHERE "users"."email" IS NOT NULL""");
    }

    @Test
    void betweenConvenienceMethods() {
        // Test LocalDate between
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        String sql = new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where("birth_date")
                .between(startDate, endDate)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."birth_date" FROM "users" WHERE ("users"."birth_date" >= '2024-01-01') AND ("users"."birth_date" <= '2024-12-31')""");

        // Test LocalDateTime between
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 3, 31, 23, 59, 59);
        sql = new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where("created_at")
                .between(startDateTime, endDateTime)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "posts"."created_at" FROM "posts" WHERE ("posts"."created_at" >= '2024-03-01T00:00') AND ("posts"."created_at" <= '2024-03-31T23:59:59')""");

        // Test Number between
        sql = new SelectBuilder(renderer, "age")
                .from("users")
                .where("age")
                .between(18, 65)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."age" FROM "users" WHERE ("users"."age" >= 18) AND ("users"."age" <= 65)""");
    }

    @Test
    void logicalOperators() {
        // Test AND with different types
        String sql = new SelectBuilder(renderer, "name", "age")
                .from("users")
                .where("name")
                .eq("John")
                .and("age")
                .gt(25)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."name", "users"."age" FROM "users" WHERE ("users"."name" = 'John') AND ("users"."age" > 25)""");

        // Test OR with different types
        sql = new SelectBuilder(renderer, "name", "age")
                .from("users")
                .where("age")
                .lt(18)
                .or("active")
                .eq(false)
                .build();
        assertThat(sql)
                .isEqualTo(
                        """
                SELECT "users"."name", "users"."age" FROM "users" WHERE ("users"."age" < 18) OR ("users"."active" = false)""");
    }

    @Test
    void tableAliasSupport() {
        // Test with table alias
        String sql = new SelectBuilder(renderer, "name")
                .from("users")
                .as("u")
                .where("name")
                .eq("John")
                .build();
        assertThat(sql).isEqualTo("""
                SELECT "u"."name" FROM "users" AS u WHERE "u"."name" = 'John'""");
    }

    @Test
    void inOperatorWithStrings() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where("status")
                .in("active", "pending", "approved")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE "users"."status" IN('active', 'pending', 'approved')""");
    }

    @Test
    void inOperatorWithNumbers() {
        String sql = new SelectBuilder(renderer, "*")
                .from("orders")
                .where("customer_id")
                .in(100, 200, 300, 400)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "orders" WHERE "orders"."customer_id" IN(100, 200, 300, 400)""");
    }

    @Test
    void inOperatorWithBooleans() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where("active")
                .in(true, false)
                .build();

        assertThat(sql).isEqualTo("""
                SELECT * FROM "users" WHERE "users"."active" IN(true, false)""");
    }

    @Test
    void inOperatorWithDates() {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 6, 1);
        LocalDate date3 = LocalDate.of(2023, 12, 31);

        String sql = new SelectBuilder(renderer, "*")
                .from("events")
                .where("event_date")
                .in(date1, date2, date3)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "events" WHERE "events"."event_date" IN('2023-01-01', '2023-06-01', '2023-12-31')""");
    }

    @Test
    void inOperatorWithDateTimes() {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 6, 1, 15, 30);

        String sql = new SelectBuilder(renderer, "*")
                .from("logs")
                .where("created_at")
                .in(dt1, dt2)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "logs" WHERE "logs"."created_at" IN('2023-01-01T10:00', '2023-06-01T15:30')""");
    }

    @Test
    void inOperatorWithAndCondition() {
        String sql = new SelectBuilder(renderer, "*")
                .from("products")
                .where("category")
                .in("electronics", "books")
                .and("price")
                .gt(50)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "products" WHERE ("products"."category" IN('electronics', 'books')) AND ("products"."price" > 50)""");
    }

    @Test
    void inOperatorWithOrCondition() {
        String sql = new SelectBuilder(renderer, "*")
                .from("users")
                .where("role")
                .in("admin", "moderator")
                .or("status")
                .eq("verified")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE ("users"."role" IN('admin', 'moderator')) OR ("users"."status" = 'verified')""");
    }

    @Test
    void inOperatorEmptyValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .where("status")
                        .in(new String[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void inOperatorNullValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .where("status")
                        .in((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }
}
