package io.github.auspis.fluentsql4j.dsl.select;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereConditionBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void stringComparisons() throws SQLException {
        // Test string equality
        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .eq("John")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");

        // Test string inequality
        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .ne("Jane")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" <> ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Jane");

        // Test string comparisons
        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .gt("A")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" > ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "A");

        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .lt("Z")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" < ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Z");

        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .gte("B")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" >= ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "B");

        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .lte("Y")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" <= ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Y");
    }

    @Test
    void numberComparisons() throws SQLException {
        // Test integer operations
        new SelectBuilder(specFactory, "age")
                .from("users")
                .where()
                .column("age")
                .eq(25)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 25);

        new SelectBuilder(specFactory, "age")
                .from("users")
                .where()
                .column("age")
                .ne(30)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" <> ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 30);

        new SelectBuilder(specFactory, "age")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" > ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);

        new SelectBuilder(specFactory, "age")
                .from("users")
                .where()
                .column("age")
                .lt(65)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" < ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 65);

        // Test double operations
        new SelectBuilder(specFactory, "salary")
                .from("employees")
                .where()
                .column("salary")
                .gte(50000.5)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "salary" FROM "employees" WHERE "salary" >= ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50000.5);

        new SelectBuilder(specFactory, "salary")
                .from("employees")
                .where()
                .column("salary")
                .lte(100000.75)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "salary" FROM "employees" WHERE "salary" <= ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100000.75);
    }

    @Test
    void booleanComparisons() throws SQLException {
        // Test boolean equality
        new SelectBuilder(specFactory, "active")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "active" FROM "users" WHERE "active" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);

        // Test boolean inequality
        new SelectBuilder(specFactory, "active")
                .from("users")
                .where()
                .column("active")
                .ne(false)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "active" FROM "users" WHERE "active" <> ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, false);
    }

    @Test
    void localDateComparisons() throws SQLException {
        LocalDate date = LocalDate.of(2024, 3, 15);

        // Test LocalDate operations
        new SelectBuilder(specFactory, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .eq(date)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" = ?""");

        new SelectBuilder(specFactory, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .gt(date)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" > ?""");

        new SelectBuilder(specFactory, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .lt(date)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" < ?""");
        verify(sqlCaptureHelper.getPreparedStatement(), times(3)).setObject(1, date);
    }

    @Test
    void localDateTimeComparisons() throws SQLException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 30, 45);

        // Test LocalDateTime operations
        new SelectBuilder(specFactory, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .eq(dateTime)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "created_at" FROM "posts" WHERE "created_at" = ?""");

        new SelectBuilder(specFactory, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .gte(dateTime)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "created_at" FROM "posts" WHERE "created_at" >= ?""");
        verify(sqlCaptureHelper.getPreparedStatement(), times(2)).setObject(1, dateTime);
    }

    @Test
    void likePatternMatching() throws SQLException {
        new SelectBuilder(specFactory, "name")
                .from("users")
                .where()
                .column("name")
                .like("John%")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" LIKE ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John%");

        new SelectBuilder(specFactory, "email")
                .from("users")
                .where()
                .column("email")
                .like("%@example.com")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" LIKE ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%@example.com");
    }

    @Test
    void nullChecks() throws SQLException {
        // Test IS NULL
        new SelectBuilder(specFactory, "email")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" IS NULL""");

        // Test IS NOT NULL
        new SelectBuilder(specFactory, "email")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" IS NOT NULL""");
    }

    @Test
    void betweenConvenienceMethods() throws SQLException {
        // Test LocalDate between
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        new SelectBuilder(specFactory, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .between(startDate, endDate)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        SELECT "birth_date" FROM "users" WHERE "birth_date" BETWEEN ? AND ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, startDate);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, endDate);

        // Test LocalDateTime between
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 3, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .between(startDateTime, endDateTime)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "created_at" FROM "posts" WHERE "created_at" BETWEEN ? AND ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, startDateTime);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, endDateTime);

        // Test Number between
        new SelectBuilder(specFactory, "age")
                .from("users")
                .where()
                .column("age")
                .between(18, 65)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" BETWEEN ? AND ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 65);
    }

    @Test
    void logicalOperators() throws SQLException {
        // Test AND with different types
        new SelectBuilder(specFactory, "name", "age")
                .from("users")
                .where()
                .column("name")
                .eq("John")
                .and()
                .column("age")
                .gt(25)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", "age" FROM "users" WHERE ("name" = ?) AND ("age" > ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 25);

        // Test OR with different types
        new SelectBuilder(specFactory, "name", "age")
                .from("users")
                .where()
                .column("age")
                .lt(18)
                .or()
                .column("active")
                .eq(false)
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name", "age" FROM "users" WHERE ("age" < ?) OR ("active" = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void tableAliasSupport() throws SQLException {
        new SelectBuilder(specFactory, "name")
                .from("users")
                .as("u")
                .where()
                .column("name")
                .eq("John")
                .build(sqlCaptureHelper.getConnection());
        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT "name" FROM "users" AS u WHERE "name" = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
    }

    @Test
    void inOperatorWithStrings() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("status")
                .in("active", "pending", "approved")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE "status" IN (?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "pending");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "approved");
    }

    @Test
    void inOperatorWithNumbers() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("customer_id")
                .in(100, 200, 300, 400)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "orders" WHERE "customer_id" IN (?, ?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 200);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 300);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 400);
    }

    @Test
    void inOperatorWithBooleans() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("active")
                .in(true, false)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE "active" IN (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void inOperatorWithDates() throws SQLException {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 6, 1);
        LocalDate date3 = LocalDate.of(2023, 12, 31);

        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("event_date")
                .in(date1, date2, date3)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "events" WHERE "event_date" IN (?, ?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, date1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, date2);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, date3);
    }

    @Test
    void inOperatorWithDateTimes() throws SQLException {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 6, 1, 15, 30);

        new SelectBuilder(specFactory, "*")
                .from("logs")
                .where()
                .column("created_at")
                .in(dt1, dt2)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "logs" WHERE "created_at" IN (?, ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, dt1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, dt2);
    }

    @Test
    void inOperatorWithAndCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("category")
                .in("electronics", "books")
                .and()
                .column("price")
                .gt(50)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "products" WHERE ("category" IN (?, ?)) AND ("price" > ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "books");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 50);
    }

    @Test
    void inOperatorWithOrCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .column("role")
                .in("admin", "moderator")
                .or()
                .column("status")
                .eq("verified")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE ("role" IN (?, ?)) OR ("status" = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "admin");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "moderator");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "verified");
    }

    @Test
    void inOperatorEmptyValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .where()
                        .column("status")
                        .in(new String[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void inOperatorNullValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "*")
                        .from("users")
                        .where()
                        .column("status")
                        .in((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void jsonValueStringComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.name")
                .eq("John")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("profile", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.name");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "John");
    }

    @Test
    void jsonValueNumberComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.age")
                .gt(18)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("profile", ?) > ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.age");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 18);
    }

    @Test
    void jsonValueNestedPathComparison() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("customers")
                .where()
                .jsonValue("details", "$.address.city")
                .eq("New York")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "customers" WHERE JSON_VALUE("details", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.address.city");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "New York");
    }

    @Test
    void jsonValueIsNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonValue("metadata", "$.discount")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "products" WHERE JSON_VALUE("metadata", ?) IS NULL""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.discount");
    }

    @Test
    void jsonExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("settings")
                .where()
                .jsonExists("config", "$.theme")
                .exists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "settings" WHERE JSON_EXISTS("config", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.theme");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void jsonNotExistsCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("items")
                .where()
                .jsonExists("tags", "$.featured")
                .notExists()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "items" WHERE JSON_EXISTS("tags", ?) = ?""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.featured");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void jsonValueWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.verified")
                .eq("true")
                .and()
                .column("active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE (JSON_VALUE("profile", ?) = ?) AND ("active" = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.verified");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "true");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, true);
    }

    @Test
    void jsonValueWithOrCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .where()
                .jsonValue("role", "$.type")
                .eq("admin")
                .or()
                .jsonValue("role", "$.type")
                .eq("moderator")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "users" WHERE (JSON_VALUE("role", ?) = ?) OR (JSON_VALUE("role", ?) = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.type");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "admin");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.type");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "moderator");
    }

    @Test
    void jsonQueryIsNotNull() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("records")
                .where()
                .jsonQuery("content", "$.data.items")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "records" WHERE JSON_QUERY("content", ?) IS NOT NULL""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.data.items");
    }

    @Test
    void jsonValueWithBetweenCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .jsonValue("metadata", "$.price")
                .gte(10)
                .and()
                .jsonValue("metadata", "$.price")
                .lte(100)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT * FROM "products" WHERE (JSON_VALUE("metadata", ?) >= ?) AND (JSON_VALUE("metadata", ?) <= ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "$.price");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "$.price");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 100);
    }

    // Cross-table WHERE conditions (multi-table context)
    @Test
    void crossTableWhereWithExplicitAliasAndColumn() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("orders")
                .as("o")
                .on("u", "id", "o", "user_id")
                .where()
                .column("u", "age")
                .gt(18)
                .and()
                .column("o", "status")
                .eq("COMPLETED")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("\"u\".\"age\" > ?")
                .contains("\"o\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 18);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "COMPLETED");
    }

    @Test
    void crossTableWhereWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("customers")
                .as("c")
                .leftJoin("orders")
                .as("o")
                .on("c", "id", "o", "customer_id")
                .where()
                .column("c", "country")
                .eq("IT")
                .and()
                .column("o", "total")
                .gte(1000)
                .and()
                .column("c", "active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHERE")
                .contains("\"c\".\"country\" = ?")
                .contains("\"o\".\"total\" >= ?")
                .contains("\"c\".\"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "IT");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1000);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, true);
    }

    @Test
    void crossTableWhereWithDateComparisons() throws SQLException {
        LocalDate cutoffDate = LocalDate.of(2024, 1, 1);

        new SelectBuilder(specFactory, "*")
                .from("employees")
                .as("e")
                .innerJoin("departments")
                .as("d")
                .on("e", "dept_id", "d", "id")
                .where()
                .column("e", "hire_date")
                .gte(cutoffDate)
                .and()
                .column("d", "active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"e\".\"hire_date\" >= ?").contains("\"d\".\"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, cutoffDate);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void crossTableWhereWithNullChecks() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .leftJoin("profiles")
                .as("p")
                .on("u", "id", "p", "user_id")
                .where()
                .column("u", "email")
                .isNotNull()
                .and()
                .column("p", "phone")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"u\".\"email\" IS NOT NULL").contains("\"p\".\"phone\" IS NULL");
    }

    @Test
    void crossTableWhereWithInOperator() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .where()
                .column("o", "status")
                .in("PENDING", "PROCESSING", "COMPLETED")
                .and()
                .column("c", "country")
                .in("IT", "FR", "DE")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"o\".\"status\" IN (?, ?, ?)")
                .contains("\"c\".\"country\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "PENDING");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "PROCESSING");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "COMPLETED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "IT");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, "FR");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, "DE");
    }

    @Test
    void crossTableWhereWithBetween() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .as("p")
                .innerJoin("categories")
                .as("c")
                .on("p", "category_id", "c", "id")
                .where()
                .column("p", "price")
                .between(10.0, 100.0)
                .and()
                .column("c", "priority")
                .between(1, 5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"p\".\"price\" BETWEEN ? AND ?")
                .contains("\"c\".\"priority\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10.0);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100.0);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 5);
    }

    @Test
    void crossTableWhereWithLike() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .leftJoin("profiles")
                .as("p")
                .on("u", "id", "p", "user_id")
                .where()
                .column("u", "username")
                .like("admin%")
                .and()
                .column("p", "bio")
                .like("%developer%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"u\".\"username\" LIKE ?").contains("\"p\".\"bio\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "admin%");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "%developer%");
    }

    // Validation tests for cross-table WHERE
    @Test
    void crossTableWhereRejectsNullAlias() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column(null, "age")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference cannot be null or empty");
    }

    @Test
    void crossTableWhereRejectsEmptyAlias() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("", "age")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference cannot be null or empty");
    }

    @Test
    void crossTableWhereRejectsAliasWithDot() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u.x", "age")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference must not contain dot");
    }

    @Test
    void crossTableWhereRejectsNullColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", null)
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void crossTableWhereRejectsEmptyColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void crossTableWhereRejectsColumnWithDot() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "users.age")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot");
    }

    @Test
    void singleTableWhereRejectsDotNotation() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .where()
                        .column("users.age")
                        .gt(18))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot notation");
    }

    // Column-to-column comparisons (new feature)
    @Test
    void columnToColumnComparisonWithEq() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("employees")
                .as("e")
                .on("u", "id", "e", "user_id")
                .where()
                .column("u", "age")
                .eq()
                .column("e", "age")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"u\".\"age\" = \"e\".\"age\"");
    }

    @Test
    void columnToColumnComparisonWithGt() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("employees")
                .as("e")
                .on("u", "id", "e", "user_id")
                .where()
                .column("u", "age")
                .gt()
                .column("e", "age")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"u\".\"age\" > \"e\".\"age\"");
    }

    @Test
    void columnToColumnComparisonWithLt() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .as("o")
                .innerJoin("expected_orders")
                .as("eo")
                .on("o", "id", "eo", "order_id")
                .where()
                .column("o", "total")
                .lt()
                .column("eo", "total")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"o\".\"total\" < \"eo\".\"total\"");
    }

    @Test
    void columnToColumnComparisonWithGte() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .as("p")
                .innerJoin("thresholds")
                .as("t")
                .on("p", "id", "t", "product_id")
                .where()
                .column("p", "price")
                .gte()
                .column("t", "min_price")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"p\".\"price\" >= \"t\".\"min_price\"");
    }

    @Test
    void columnToColumnComparisonWithLte() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("budget")
                .as("b")
                .innerJoin("spending")
                .as("s")
                .on("b", "id", "s", "budget_id")
                .where()
                .column("s", "amount")
                .lte()
                .column("b", "limit")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"s\".\"amount\" <= \"b\".\"limit\"");
    }

    @Test
    void columnToColumnComparisonWithNe() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("current")
                .as("c")
                .innerJoin("previous")
                .as("p")
                .on("c", "id", "p", "id")
                .where()
                .column("c", "status")
                .ne()
                .column("p", "status")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"c\".\"status\" <> \"p\".\"status\"");
    }

    @Test
    void columnToColumnComparisonWithAndCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("users")
                .as("u")
                .innerJoin("profiles")
                .as("p")
                .on("u", "id", "p", "user_id")
                .where()
                .column("u", "age")
                .gt()
                .column("p", "age")
                .and()
                .column("u", "status")
                .eq("active")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"u\".\"age\" > \"p\".\"age\"")
                .contains("\"u\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
    }

    @Test
    void columnToColumnComparisonWithOrCondition() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("left_table")
                .as("l")
                .innerJoin("right_table")
                .as("r")
                .on("l", "id", "r", "id")
                .where()
                .column("l", "value")
                .eq()
                .column("r", "value")
                .or()
                .column("l", "status")
                .eq("pending")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"l\".\"value\" = \"r\".\"value\"")
                .contains("\"l\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "pending");
    }

    @Test
    void columnToColumnComparisonRejectsNullAliasInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .gt()
                        .column(null, "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonRejectsEmptyAliasInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .eq()
                        .column("", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonRejectsAliasWithDotInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .ne()
                        .column("u.x", "age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Table reference must not contain dot");
    }

    @Test
    void columnToColumnComparisonRejectsNullColumnInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .lte()
                        .column("e", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonRejectsEmptyColumnInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .gte()
                        .column("e", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonRejectsColumnWithDotInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory)
                        .from("users")
                        .as("u")
                        .where()
                        .column("u", "age")
                        .lt()
                        .column("e", "table.age"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot");
    }
}
