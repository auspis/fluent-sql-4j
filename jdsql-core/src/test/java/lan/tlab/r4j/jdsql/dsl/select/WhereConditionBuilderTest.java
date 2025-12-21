package lan.tlab.r4j.jdsql.dsl.select;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
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
}
