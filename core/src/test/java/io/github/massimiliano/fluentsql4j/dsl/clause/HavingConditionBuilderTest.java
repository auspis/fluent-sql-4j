package io.github.massimiliano.fluentsql4j.dsl.clause;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.select.SelectBuilder;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                .groupBy()
                .column("category")
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
                .groupBy()
                .column("category")
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
                .groupBy()
                .column("name")
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
                .groupBy()
                .column("name")
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
                .groupBy()
                .column("code")
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
                .groupBy()
                .column("code")
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
                .groupBy()
                .column("customer_id")
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
                .groupBy()
                .column("total")
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
                .groupBy()
                .column("customer_id")
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
                .groupBy()
                .column("category_id")
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
                .groupBy()
                .column("total")
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
                .groupBy()
                .column("total")
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
                .groupBy()
                .column("active")
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
                .groupBy()
                .column("verified")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("email")
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
                .groupBy()
                .column("email")
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
                .groupBy()
                .column("name")
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
                .groupBy()
                .column("name")
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
                .groupBy()
                .column("category")
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
                .groupBy()
                .column("status_id")
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
                .groupBy()
                .column("status")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("total")
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
                .groupBy()
                .column("total")
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
                .groupBy()
                .column("event_date")
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
                .groupBy()
                .column("event_time")
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
                .groupBy()
                .column("dept_id")
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
                .groupBy()
                .column("dept_id")
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
                .groupBy()
                .column("avg_salary")
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
                .groupBy()
                .column("avg_salary")
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
                .groupBy()
                .column("account_id")
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
                .groupBy()
                .column("account_id")
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
                .groupBy()
                .column("customer_id")
                .having()
                .column("customer_id")
                .gt(100)
                .andHaving()
                .column("customer_id")
                .lt(500)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING (\"customer_id\" > ?) AND (\"customer_id\" < ?)");
    }

    // Multiple HAVING conditions with OR
    @Test
    void multipleHavingConditionsWithOr() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy()
                .column("status")
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
                .groupBy()
                .column("customer_id")
                .column("status")
                .having()
                .column("customer_id")
                .gte(1)
                .andHaving()
                .column("status")
                .in("active", "pending")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("""
                GROUP BY \"customer_id\", \"status\" \
                HAVING (\"customer_id\" >= ?) \
                AND (\"status\" IN (?, ?))""");
    }

    @Test
    void complexHavingWithDateRangeAndInList() throws SQLException {
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy()
                .column("event_date")
                .column("category")
                .having()
                .column("event_date")
                .between(startDate, endDate)
                .andHaving()
                .column("category")
                .in("A", "B", "C")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("""
                GROUP BY \"event_date\", \"category\" \
                HAVING (\"event_date\" BETWEEN ? AND ?) \
                AND (\"category\" IN (?, ?, ?))""");
    }

    // Cross-table HAVING conditions (multi-table context)
    @Test
    void crossTableHavingWithExplicitAliasAndColumn() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .groupBy()
                .column("customer_id")
                .having()
                .column("o", "total")
                .gt(1000)
                .andHaving()
                .column("c", "country")
                .eq("IT")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                SELECT \"o\".\"COUNT(*)\" \
                FROM \"orders\" AS o \
                INNER JOIN \"customers\" AS c ON \"o\".\"customer_id\" = \"c\".\"id\" \
                GROUP BY \"o\".\"customer_id\" \
                HAVING (\"o\".\"total\" > ?) \
                AND (\"c\".\"country\" = ?)""");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1000);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "IT");
    }

    @Test
    void crossTableHavingWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("transactions")
                .as("t")
                .innerJoin("accounts")
                .as("a")
                .on("t", "account_id", "a", "id")
                .groupBy()
                .column("account_id")
                .having()
                .column("t", "amount")
                .gte(100)
                .andHaving()
                .column("a", "type")
                .eq("PREMIUM")
                .andHaving()
                .column("t", "status")
                .ne("CANCELLED")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"t\".\"amount\" >= ?")
                .contains("\"a\".\"type\" = ?")
                .contains("\"t\".\"status\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "PREMIUM");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "CANCELLED");
    }

    @Test
    void crossTableHavingWithDateComparisons() throws SQLException {
        LocalDate cutoffDate = LocalDate.of(2024, 6, 1);

        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .as("o")
                .innerJoin("customers")
                .as("c")
                .on("o", "customer_id", "c", "id")
                .groupBy()
                .column("customer_id")
                .having()
                .column("o", "created_date")
                .gte(cutoffDate)
                .andHaving()
                .column("c", "active")
                .eq(true)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"o\".\"created_date\" >= ?").contains("\"c\".\"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, cutoffDate);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
    }

    @Test
    void crossTableHavingWithNullChecks() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .as("p")
                .leftJoin("categories")
                .as("c")
                .on("p", "category_id", "c", "id")
                .groupBy()
                .column("category_id")
                .having()
                .column("p", "discount")
                .isNotNull()
                .andHaving()
                .column("c", "parent_id")
                .isNull()
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"p\".\"discount\" IS NOT NULL")
                .contains("\"c\".\"parent_id\" IS NULL");
    }

    @Test
    void crossTableHavingWithInOperator() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("sales")
                .as("s")
                .innerJoin("regions")
                .as("r")
                .on("s", "region_id", "r", "id")
                .groupBy()
                .column("region_id")
                .having()
                .column("s", "status")
                .in("COMPLETED", "SHIPPED", "DELIVERED")
                .andHaving()
                .column("r", "country")
                .in("IT", "FR", "ES")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"s\".\"status\" IN (?, ?, ?)")
                .contains("\"r\".\"country\" IN (?, ?, ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "COMPLETED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "SHIPPED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "DELIVERED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "IT");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, "FR");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, "ES");
    }

    @Test
    void crossTableHavingWithBetween() throws SQLException {
        new SelectBuilder(specFactory, "AVG(price)")
                .from("orders")
                .as("o")
                .innerJoin("products")
                .as("p")
                .on("o", "product_id", "p", "id")
                .groupBy()
                .column("o", "product_id")
                .having()
                .column("o", "quantity")
                .between(10, 100)
                .andHaving()
                .column("p", "rating")
                .between(4.0, 5.0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"o\".\"quantity\" BETWEEN ? AND ?")
                .contains("\"p\".\"rating\" BETWEEN ? AND ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 4.0);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 5.0);
    }

    @Test
    void crossTableHavingWithLike() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .as("u")
                .innerJoin("subscriptions")
                .as("s")
                .on("u", "id", "s", "user_id")
                .groupBy()
                .column("user_id")
                .having()
                .column("u", "email")
                .like("%@company.com")
                .andHaving()
                .column("s", "plan_name")
                .like("Premium%")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"u\".\"email\" LIKE ?").contains("\"s\".\"plan_name\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%@company.com");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "Premium%");
    }

    // Validation tests for cross-table HAVING
    @Test
    void crossTableHavingRejectsNullAlias() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column(null, "total")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias cannot be null or empty");
    }

    @Test
    void crossTableHavingRejectsEmptyAlias() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("", "total")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias cannot be null or empty");
    }

    @Test
    void crossTableHavingRejectsAliasWithDot() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("o.x", "total")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias must not contain dot");
    }

    @Test
    void crossTableHavingRejectsNullColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .as("o")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("o", null)
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void crossTableHavingRejectsEmptyColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .as("o")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("o", "")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name cannot be null or empty");
    }

    @Test
    void crossTableHavingRejectsColumnWithDot() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .as("o")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("o", "orders.total")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot");
    }

    @Test
    void singleTableHavingRejectsDotNotation() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .groupBy()
                        .column("customer_id")
                        .having()
                        .column("orders.total")
                        .gt(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dot notation not supported");
    }

    // Column-to-column comparisons (new feature)
    @Test
    void columnToColumnComparisonInHavingWithEq() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .as("o")
                .innerJoin("targets")
                .as("t")
                .on("o", "id", "t", "order_id")
                .groupBy()
                .column("o", "customer_id")
                .having()
                .column("o", "total")
                .eq()
                .column("t", "expected_total")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"o\".\"total\" = \"t\".\"expected_total\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithGt() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("sales")
                .as("s")
                .innerJoin("quotas")
                .as("q")
                .on("s", "region_id", "q", "region_id")
                .groupBy()
                .column("s", "region_id")
                .having()
                .column("s", "amount")
                .gt()
                .column("q", "quota")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"s\".\"amount\" > \"q\".\"quota\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithLt() throws SQLException {
        new SelectBuilder(specFactory, "AVG(price)")
                .from("items")
                .as("i")
                .innerJoin("benchmarks")
                .as("b")
                .on("i", "category_id", "b", "category_id")
                .groupBy()
                .column("i", "category_id")
                .having()
                .column("i", "avg_price")
                .lt()
                .column("b", "market_price")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"i\".\"avg_price\" < \"b\".\"market_price\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithGte() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .as("e")
                .innerJoin("thresholds")
                .as("th")
                .on("e", "type_id", "th", "type_id")
                .groupBy()
                .column("e", "type_id")
                .having()
                .column("e", "count")
                .gte()
                .column("th", "min_count")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"e\".\"count\" >= \"th\".\"min_count\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithLte() throws SQLException {
        new SelectBuilder(specFactory, "MAX(duration)")
                .from("tasks")
                .as("t")
                .innerJoin("limits")
                .as("l")
                .on("t", "category_id", "l", "category_id")
                .groupBy()
                .column("t", "category_id")
                .having()
                .column("t", "max_duration")
                .lte()
                .column("l", "time_limit")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"t\".\"max_duration\" <= \"l\".\"time_limit\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithNe() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("current_data")
                .as("c")
                .innerJoin("previous_data")
                .as("p")
                .on("c", "id", "p", "id")
                .groupBy()
                .column("c", "id")
                .having()
                .column("c", "value")
                .ne()
                .column("p", "value")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("\"c\".\"value\" <> \"p\".\"value\"");
    }

    @Test
    void columnToColumnComparisonInHavingWithAndCondition() throws SQLException {
        new SelectBuilder(specFactory, "SUM(revenue)")
                .from("sales")
                .as("s")
                .innerJoin("projections")
                .as("p")
                .on("s", "product_id", "p", "product_id")
                .groupBy()
                .column("s", "product_id")
                .having()
                .column("s", "revenue")
                .gt()
                .column("p", "projected_revenue")
                .andHaving()
                .column("s", "region")
                .eq("EMEA")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"s\".\"revenue\" > \"p\".\"projected_revenue\"")
                .contains("\"s\".\"region\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "EMEA");
    }

    @Test
    void columnToColumnComparisonInHavingWithOrCondition() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .as("o")
                .innerJoin("rules")
                .as("r")
                .on("o", "status_id", "r", "status_id")
                .groupBy()
                .column("o", "status_id")
                .having()
                .column("o", "count")
                .eq()
                .column("r", "expected_count")
                .or()
                .column("o", "status")
                .eq("pending")
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("\"o\".\"count\" = \"r\".\"expected_count\"")
                .contains("\"o\".\"status\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "pending");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsNullAliasInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "SUM(amount)")
                        .from("sales")
                        .as("s")
                        .groupBy()
                        .column("s", "region_id")
                        .having()
                        .column("s", "amount")
                        .gt()
                        .column(null, "quota"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsEmptyAliasInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("events")
                        .as("e")
                        .groupBy()
                        .column("e", "type_id")
                        .having()
                        .column("e", "count")
                        .eq()
                        .column("", "expected_count"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsAliasWithDotInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "AVG(price)")
                        .from("items")
                        .as("i")
                        .groupBy()
                        .column("i", "category_id")
                        .having()
                        .column("i", "avg_price")
                        .ne()
                        .column("b.x", "market_price"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Alias must not contain dot");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsNullColumnInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "COUNT(*)")
                        .from("orders")
                        .as("o")
                        .groupBy()
                        .column("o", "customer_id")
                        .having()
                        .column("o", "total")
                        .lte()
                        .column("t", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsEmptyColumnInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "MAX(duration)")
                        .from("tasks")
                        .as("t")
                        .groupBy()
                        .column("t", "category_id")
                        .having()
                        .column("t", "max_duration")
                        .gte()
                        .column("l", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column cannot be null or empty");
    }

    @Test
    void columnToColumnComparisonInHavingRejectsColumnWithDotInRightColumn() {
        assertThatThrownBy(() -> new SelectBuilder(specFactory, "SUM(revenue)")
                        .from("sales")
                        .as("s")
                        .groupBy()
                        .column("s", "product_id")
                        .having()
                        .column("s", "revenue")
                        .lt()
                        .column("p", "table.projected_revenue"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column must not contain dot");
    }
}
