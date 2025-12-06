package lan.tlab.r4j.jdsql.dsl.select;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class WhereConditionBuilderTest {

    private DialectRenderer renderer;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        renderer = StandardSqlRendererFactory.dialectRendererStandardSql();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void stringComparisons() throws SQLException {
        // Test string equality
        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .eq("John")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" = ?""");
        verify(ps).setObject(1, "John");

        // Test string inequality
        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .ne("Jane")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" <> ?""");
        verify(ps).setObject(1, "Jane");

        // Test string comparisons
        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .gt("A")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" > ?""");
        verify(ps).setObject(1, "A");

        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .lt("Z")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" < ?""");
        verify(ps).setObject(1, "Z");

        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .gte("B")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" >= ?""");
        verify(ps).setObject(1, "B");

        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .lte("Y")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" <= ?""");
        verify(ps).setObject(1, "Y");
    }

    @Test
    void numberComparisons() throws SQLException {
        // Test integer operations
        new SelectBuilder(renderer, "age")
                .from("users")
                .where()
                .column("age")
                .eq(25)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" = ?""");
        verify(ps).setObject(1, 25);

        new SelectBuilder(renderer, "age")
                .from("users")
                .where()
                .column("age")
                .ne(30)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" <> ?""");
        verify(ps).setObject(1, 30);

        new SelectBuilder(renderer, "age")
                .from("users")
                .where()
                .column("age")
                .gt(18)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" > ?""");
        verify(ps).setObject(1, 18);

        new SelectBuilder(renderer, "age")
                .from("users")
                .where()
                .column("age")
                .lt(65)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue()).isEqualTo("""
                SELECT "age" FROM "users" WHERE "age" < ?""");
        verify(ps).setObject(1, 65);

        // Test double operations
        new SelectBuilder(renderer, "salary")
                .from("employees")
                .where()
                .column("salary")
                .gte(50000.5)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "salary" FROM "employees" WHERE "salary" >= ?""");
        verify(ps).setObject(1, 50000.5);

        new SelectBuilder(renderer, "salary")
                .from("employees")
                .where()
                .column("salary")
                .lte(100000.75)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "salary" FROM "employees" WHERE "salary" <= ?""");
        verify(ps).setObject(1, 100000.75);
    }

    @Test
    void booleanComparisons() throws SQLException {
        // Test boolean equality
        new SelectBuilder(renderer, "active")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "active" FROM "users" WHERE "active" = ?""");
        verify(ps).setObject(1, true);

        // Test boolean inequality
        new SelectBuilder(renderer, "active")
                .from("users")
                .where()
                .column("active")
                .ne(false)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "active" FROM "users" WHERE "active" <> ?""");
        verify(ps).setObject(1, false);
    }

    @Test
    void localDateComparisons() throws SQLException {
        LocalDate date = LocalDate.of(2024, 3, 15);

        // Test LocalDate operations
        new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .eq(date)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" = ?""");

        new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .gt(date)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" > ?""");

        new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .lt(date)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "birth_date" FROM "users" WHERE "birth_date" < ?""");
        verify(ps, times(3)).setObject(1, date);
    }

    @Test
    void localDateTimeComparisons() throws SQLException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 10, 30, 45);

        // Test LocalDateTime operations
        new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .eq(dateTime)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "created_at" FROM "posts" WHERE "created_at" = ?""");

        new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .gte(dateTime)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "created_at" FROM "posts" WHERE "created_at" >= ?""");
        verify(ps, times(2)).setObject(1, dateTime);
    }

    @Test
    void likePatternMatching() throws SQLException {
        new SelectBuilder(renderer, "name")
                .from("users")
                .where()
                .column("name")
                .like("John%")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "name" FROM "users" WHERE "name" LIKE ?""");
        verify(ps).setObject(1, "John%");

        new SelectBuilder(renderer, "email")
                .from("users")
                .where()
                .column("email")
                .like("%@example.com")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" LIKE ?""");
        verify(ps).setObject(1, "%@example.com");
    }

    @Test
    void nullChecks() throws SQLException {
        // Test IS NULL
        new SelectBuilder(renderer, "email")
                .from("users")
                .where()
                .column("email")
                .isNull()
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" IS NULL""");

        // Test IS NOT NULL
        new SelectBuilder(renderer, "email")
                .from("users")
                .where()
                .column("email")
                .isNotNull()
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "email" FROM "users" WHERE "email" IS NOT NULL""");
    }

    @Test
    void betweenConvenienceMethods() throws SQLException {
        // Test LocalDate between
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        new SelectBuilder(renderer, "birth_date")
                .from("users")
                .where()
                .column("birth_date")
                .between(startDate, endDate)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                        SELECT "birth_date" FROM "users" WHERE ("birth_date" >= ?) AND ("birth_date" <= ?)""");
        verify(ps).setObject(1, startDate);
        verify(ps).setObject(2, endDate);

        // Test LocalDateTime between
        LocalDateTime startDateTime = LocalDateTime.of(2024, 3, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 3, 31, 23, 59, 59);
        new SelectBuilder(renderer, "created_at")
                .from("posts")
                .where()
                .column("created_at")
                .between(startDateTime, endDateTime)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "created_at" FROM "posts" WHERE ("created_at" >= ?) AND ("created_at" <= ?)""");
        verify(ps).setObject(1, startDateTime);
        verify(ps).setObject(2, endDateTime);

        // Test Number between
        new SelectBuilder(renderer, "age")
                .from("users")
                .where()
                .column("age")
                .between(18, 65)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "age" FROM "users" WHERE ("age" >= ?) AND ("age" <= ?)""");
        verify(ps).setObject(1, 18);
        verify(ps).setObject(2, 65);
    }

    @Test
    void logicalOperators() throws SQLException {
        // Test AND with different types
        new SelectBuilder(renderer, "name", "age")
                .from("users")
                .where()
                .column("name")
                .eq("John")
                .and()
                .column("age")
                .gt(25)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "name", "age" FROM "users" WHERE ("name" = ?) AND ("age" > ?)""");
        verify(ps).setObject(1, "John");
        verify(ps).setObject(2, 25);

        // Test OR with different types
        new SelectBuilder(renderer, "name", "age")
                .from("users")
                .where()
                .column("age")
                .lt(18)
                .or()
                .column("active")
                .eq(false)
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT "name", "age" FROM "users" WHERE ("age" < ?) OR ("active" = ?)""");
        verify(ps).setObject(1, 18);
        verify(ps).setObject(2, false);
    }

    @Test
    void tableAliasSupport() throws SQLException {
        new SelectBuilder(renderer, "name")
                .from("users")
                .as("u")
                .where()
                .column("name")
                .eq("John")
                .buildPreparedStatement(connection);
        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT "name" FROM "users" AS u WHERE "name" = ?""");
        verify(ps).setObject(1, "John");
    }

    @Test
    void inOperatorWithStrings() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .column("status")
                .in("active", "pending", "approved")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE "status" IN (?, ?, ?)""");
        verify(ps).setObject(1, "active");
        verify(ps).setObject(2, "pending");
        verify(ps).setObject(3, "approved");
    }

    @Test
    void inOperatorWithNumbers() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("orders")
                .where()
                .column("customer_id")
                .in(100, 200, 300, 400)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "orders" WHERE "customer_id" IN (?, ?, ?, ?)""");
        verify(ps).setObject(1, 100);
        verify(ps).setObject(2, 200);
        verify(ps).setObject(3, 300);
        verify(ps).setObject(4, 400);
    }

    @Test
    void inOperatorWithBooleans() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .column("active")
                .in(true, false)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE "active" IN (?, ?)""");
        verify(ps).setObject(1, true);
        verify(ps).setObject(2, false);
    }

    @Test
    void inOperatorWithDates() throws SQLException {
        LocalDate date1 = LocalDate.of(2023, 1, 1);
        LocalDate date2 = LocalDate.of(2023, 6, 1);
        LocalDate date3 = LocalDate.of(2023, 12, 31);

        new SelectBuilder(renderer, "*")
                .from("events")
                .where()
                .column("event_date")
                .in(date1, date2, date3)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "events" WHERE "event_date" IN (?, ?, ?)""");
        verify(ps).setObject(1, date1);
        verify(ps).setObject(2, date2);
        verify(ps).setObject(3, date3);
    }

    @Test
    void inOperatorWithDateTimes() throws SQLException {
        LocalDateTime dt1 = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2023, 6, 1, 15, 30);

        new SelectBuilder(renderer, "*")
                .from("logs")
                .where()
                .column("created_at")
                .in(dt1, dt2)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "logs" WHERE "created_at" IN (?, ?)""");
        verify(ps).setObject(1, dt1);
        verify(ps).setObject(2, dt2);
    }

    @Test
    void inOperatorWithAndCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("products")
                .where()
                .column("category")
                .in("electronics", "books")
                .and()
                .column("price")
                .gt(50)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "products" WHERE ("category" IN (?, ?)) AND ("price" > ?)""");
        verify(ps).setObject(1, "electronics");
        verify(ps).setObject(2, "books");
        verify(ps).setObject(3, 50);
    }

    @Test
    void inOperatorWithOrCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .column("role")
                .in("admin", "moderator")
                .or()
                .column("status")
                .eq("verified")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE ("role" IN (?, ?)) OR ("status" = ?)""");
        verify(ps).setObject(1, "admin");
        verify(ps).setObject(2, "moderator");
        verify(ps).setObject(3, "verified");
    }

    @Test
    void inOperatorEmptyValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .where()
                        .column("status")
                        .in(new String[0]))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void inOperatorNullValues_throwsException() {
        assertThatThrownBy(() -> new SelectBuilder(renderer, "*")
                        .from("users")
                        .where()
                        .column("status")
                        .in((String[]) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("At least one value must be provided for IN clause");
    }

    @Test
    void jsonValueStringComparison() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.name")
                .eq("John")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("profile", ?) = ?""");
        verify(ps).setObject(1, "$.name");
        verify(ps).setObject(2, "John");
    }

    @Test
    void jsonValueNumberComparison() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.age")
                .gt(18)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "users" WHERE JSON_VALUE("profile", ?) > ?""");
        verify(ps).setObject(1, "$.age");
        verify(ps).setObject(2, 18);
    }

    @Test
    void jsonValueNestedPathComparison() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("customers")
                .where()
                .jsonValue("details", "$.address.city")
                .eq("New York")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "customers" WHERE JSON_VALUE("details", ?) = ?""");
        verify(ps).setObject(1, "$.address.city");
        verify(ps).setObject(2, "New York");
    }

    @Test
    void jsonValueIsNull() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("products")
                .where()
                .jsonValue("metadata", "$.discount")
                .isNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "products" WHERE JSON_VALUE("metadata", ?) IS NULL""");
        verify(ps).setObject(1, "$.discount");
    }

    @Test
    void jsonExistsCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("settings")
                .where()
                .jsonExists("config", "$.theme")
                .exists()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "settings" WHERE JSON_EXISTS("config", ?) = ?""");
        verify(ps).setObject(1, "$.theme");
        verify(ps).setObject(2, true);
    }

    @Test
    void jsonNotExistsCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("items")
                .where()
                .jsonExists("tags", "$.featured")
                .notExists()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "items" WHERE JSON_EXISTS("tags", ?) = ?""");
        verify(ps).setObject(1, "$.featured");
        verify(ps).setObject(2, false);
    }

    @Test
    void jsonValueWithMultipleConditions() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("profile", "$.verified")
                .eq("true")
                .and()
                .column("active")
                .eq(true)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("profile", ?) = ?) AND ("active" = ?)""");
        verify(ps).setObject(1, "$.verified");
        verify(ps).setObject(2, "true");
        verify(ps).setObject(3, true);
    }

    @Test
    void jsonValueWithOrCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("users")
                .where()
                .jsonValue("role", "$.type")
                .eq("admin")
                .or()
                .jsonValue("role", "$.type")
                .eq("moderator")
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "users" WHERE (JSON_VALUE("role", ?) = ?) OR (JSON_VALUE("role", ?) = ?)""");
        verify(ps).setObject(1, "$.type");
        verify(ps).setObject(2, "admin");
        verify(ps).setObject(3, "$.type");
        verify(ps).setObject(4, "moderator");
    }

    @Test
    void jsonQueryIsNotNull() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("records")
                .where()
                .jsonQuery("content", "$.data.items")
                .isNotNull()
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo("""
                SELECT * FROM "records" WHERE JSON_QUERY("content", ?) IS NOT NULL""");
        verify(ps).setObject(1, "$.data.items");
    }

    @Test
    void jsonValueWithBetweenCondition() throws SQLException {
        new SelectBuilder(renderer, "*")
                .from("products")
                .where()
                .jsonValue("metadata", "$.price")
                .gte(10)
                .and()
                .jsonValue("metadata", "$.price")
                .lte(100)
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .isEqualTo(
                        """
                SELECT * FROM "products" WHERE (JSON_VALUE("metadata", ?) >= ?) AND (JSON_VALUE("metadata", ?) <= ?)""");
        verify(ps).setObject(1, "$.price");
        verify(ps).setObject(2, 10);
        verify(ps).setObject(3, "$.price");
        verify(ps).setObject(4, 100);
    }
}
