package lan.tlab.r4j.jdsql.dsl.clause;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HavingConditionBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    // String comparisons
    @Test
    void stringEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .eq("electronics")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
    }

    @Test
    void stringNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .ne("discontinued")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "discontinued");
    }

    @Test
    void stringGreaterThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having()
                .column("name")
                .gt("Widget")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Widget");
    }

    @Test
    void stringLessThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having()
                .column("name")
                .lt("Zebra")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Zebra");
    }

    @Test
    void stringGreaterThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("code")
                .having()
                .column("code")
                .gte("A001")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"code\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "A001");
    }

    @Test
    void stringLessThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("code")
                .having()
                .column("code")
                .lte("Z999")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"code\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Z999");
    }

    // Number comparisons
    @Test
    void numberEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("orders")
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .eq(42)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"customer_id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 42);
    }

    @Test
    void numberNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .ne(0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 0);
    }

    @Test
    void numberGreaterThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("orders")
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .gt(100)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"customer_id\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
    }

    @Test
    void numberLessThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "AVG(price)")
                .from("products")
                .groupBy("category_id")
                .having()
                .column("category_id")
                .lt(50)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category_id\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50);
    }

    @Test
    void numberGreaterThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .gte(100.5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100.5);
    }

    @Test
    void numberLessThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .lte(500.99)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 500.99);
    }

    // Boolean comparisons
    @Test
    void booleanEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("active")
                .having()
                .column("active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void booleanNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("verified")
                .having()
                .column("verified")
                .ne(false)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"verified\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, false);
    }

    // LocalDate comparisons
    @Test
    void localDateEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 12, 20);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .eq(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateNotEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .ne(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateGreaterThanComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2020, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .gt(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateLessThanComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2030, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .lt(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateGreaterThanOrEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .gte(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateLessThanOrEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .lte(testDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    // LocalDateTime comparisons
    @Test
    void localDateTimeEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 12, 20, 10, 30, 45);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .eq(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeNotEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .ne(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeGreaterThanComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .gt(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeLessThanComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .lt(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeGreaterThanOrEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .gte(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeLessThanOrEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .lte(testDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    // NULL checks
    @Test
    void isNullCheck() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("email")
                .having()
                .column("email")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NULL");
    }

    @Test
    void isNotNullCheck() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("email")
                .having()
                .column("email")
                .isNotNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NOT NULL");
    }

    // LIKE operator
    @Test
    void likeOperatorPattern() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having()
                .column("name")
                .like("%electronics%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%electronics%");
    }

    @Test
    void likeOperatorStartsWith() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having()
                .column("name")
                .like("Widget%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Widget%");
    }

    // IN operator
    @Test
    void inOperatorStrings() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having()
                .column("category")
                .in("electronics", "books", "toys")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "books");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "toys");
    }

    @Test
    void inOperatorNumbers() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("status_id")
                .having()
                .column("status_id")
                .in(1, 2, 3, 4, 5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"status_id\" IN (?, ?, ?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 2);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 3);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 4);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, 5);
    }

    @Test
    void inOperatorBooleans() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("status")
                .having()
                .column("status")
                .in(true, false)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"status\" IN (?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, false);
    }

    @Test
    void inOperatorDates() throws SQLException {
        LocalDate date1 = LocalDate.of(2025, 1, 1);
        LocalDate date2 = LocalDate.of(2025, 6, 15);
        LocalDate date3 = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .in(date1, date2, date3)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, date1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, date2);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, date3);
    }

    @Test
    void inOperatorDateTimes() throws SQLException {
        LocalDateTime dt1 = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        LocalDateTime dt2 = LocalDateTime.of(2025, 6, 15, 15, 30, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .in(dt1, dt2)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" IN (?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, dt1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, dt2);
    }

    // BETWEEN operator
    @Test
    void betweenNumbers() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .between(100, 500)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 500);
    }

    @Test
    void betweenDecimalNumbers() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having()
                .column("total")
                .between(99.99, 499.99)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 99.99);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 499.99);
    }

    @Test
    void betweenDates() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having()
                .column("event_date")
                .between(startDate, endDate)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, startDate);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, endDate);
    }

    @Test
    void betweenDateTimes() throws SQLException {
        LocalDateTime startDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having()
                .column("event_time")
                .between(startDateTime, endDateTime)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, startDateTime);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, endDateTime);
    }

    // Subquery comparisons
    @Test
    void subqueryEqualComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "MAX(salary)").from("employees");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("departments")
                .groupBy("dept_id")
                .having()
                .column("dept_id")
                .eq(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains("=")
                .contains("SELECT")
                .contains("MAX");
    }

    @Test
    void subqueryNotEqualComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "MIN(salary)").from("employees");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("departments")
                .groupBy("dept_id")
                .having()
                .column("dept_id")
                .ne(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains("<>")
                .contains("SELECT")
                .contains("MIN");
    }

    @Test
    void subqueryGreaterThanComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "AVG(salary)").from("employees");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("departments")
                .groupBy("avg_salary")
                .having()
                .column("avg_salary")
                .gt(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains(">")
                .contains("SELECT")
                .contains("AVG");
    }

    @Test
    void subqueryLessThanComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "AVG(salary)").from("employees");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("departments")
                .groupBy("avg_salary")
                .having()
                .column("avg_salary")
                .lt(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains("<")
                .contains("SELECT")
                .contains("AVG");
    }

    @Test
    void subqueryGreaterThanOrEqualComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "SUM(amount)").from("transactions");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("accounts")
                .groupBy("account_id")
                .having()
                .column("account_id")
                .gte(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains(">=")
                .contains("SELECT")
                .contains("SUM");
    }

    @Test
    void subqueryLessThanOrEqualComparison() throws SQLException {
        SelectBuilder subquery = new SelectBuilder(specFactory, "SUM(amount)").from("transactions");
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("accounts")
                .groupBy("account_id")
                .having()
                .column("account_id")
                .lte(subquery)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains("<=")
                .contains("SELECT")
                .contains("SUM");
    }

    // Multiple HAVING conditions with AND
    @Test
    void multipleHavingConditionsWithAnd() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("customer_id")
                .having()
                .column("customer_id")
                .gt(100)
                .andHaving()
                .column("customer_id")
                .lt(500)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains(">")
                .contains("<")
                .contains("AND");
    }

    // Multiple HAVING conditions with OR
    @Test
    void multipleHavingConditionsWithOr() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("status")
                .having()
                .column("status")
                .eq("pending")
                .orHaving()
                .column("status")
                .eq("cancelled")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING").contains("=").contains("OR");
    }

    // Complex combinations
    @Test
    void complexHavingWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*), SUM(amount)")
                .from("orders")
                .groupBy("customer_id", "status")
                .having()
                .column("customer_id")
                .gte(1)
                .andHaving()
                .column("status")
                .in("active", "pending")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("GROUP BY")
                .contains("HAVING")
                .contains(">=")
                .contains("IN")
                .contains("AND");
    }

    @Test
    void complexHavingWithDateRangeAndInList() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date", "category")
                .having()
                .column("event_date")
                .between(startDate, endDate)
                .andHaving()
                .column("category")
                .in("A", "B", "C")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains("BETWEEN")
                .contains("IN")
                .contains("AND");
    }
}
