package lan.tlab.r4j.jdsql.dsl.clause;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HavingConditionBuilderTest {
    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
    }

    // String comparisons
    @Test
    void stringEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having("category")
                .eq("electronics")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "electronics");
    }

    @Test
    void stringNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having("category")
                .ne("discontinued")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "discontinued");
    }

    @Test
    void stringGreaterThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having("name")
                .gt("Widget")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Widget");
    }

    @Test
    void stringLessThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having("name")
                .lt("Zebra")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Zebra");
    }

    @Test
    void stringGreaterThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("code")
                .having("code")
                .gte("A001")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"code\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "A001");
    }

    @Test
    void stringLessThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("code")
                .having("code")
                .lte("Z999")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"code\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Z999");
    }

    // Number comparisons
    @Test
    void numberEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("orders")
                .groupBy("customer_id")
                .having("customer_id")
                .eq(42)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"customer_id\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 42);
    }

    @Test
    void numberNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having("total")
                .ne(0)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 0);
    }

    @Test
    void numberGreaterThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "SUM(amount)")
                .from("orders")
                .groupBy("customer_id")
                .having("customer_id")
                .gt(100)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"customer_id\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
    }

    @Test
    void numberLessThanComparison() throws SQLException {
        new SelectBuilder(specFactory, "AVG(price)")
                .from("products")
                .groupBy("category_id")
                .having("category_id")
                .lt(50)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"category_id\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50);
    }

    @Test
    void numberGreaterThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having("total")
                .gte(100.5)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100.5);
    }

    @Test
    void numberLessThanOrEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having("total")
                .lte(500.99)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"total\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 500.99);
    }

    // Boolean comparisons
    @Test
    void booleanEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("active")
                .having("active")
                .eq(true)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"active\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void booleanNotEqualComparison() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("verified")
                .having("verified")
                .ne(false)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("event_date")
                .eq(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateNotEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having("event_date")
                .ne(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateGreaterThanComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2020, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having("event_date")
                .gt(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateLessThanComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2030, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having("event_date")
                .lt(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateGreaterThanOrEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 1, 1);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having("event_date")
                .gte(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_date\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDate);
    }

    @Test
    void localDateLessThanOrEqualComparison() throws SQLException {
        LocalDate testDate = LocalDate.of(2025, 12, 31);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_date")
                .having("event_date")
                .lte(testDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("event_time")
                .eq(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" = ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeNotEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having("event_time")
                .ne(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" <> ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeGreaterThanComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2020, 1, 1, 12, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having("event_time")
                .gt(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" > ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeLessThanComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2030, 12, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having("event_time")
                .lt(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" < ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeGreaterThanOrEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having("event_time")
                .gte(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" >= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    @Test
    void localDateTimeLessThanOrEqualComparison() throws SQLException {
        LocalDateTime testDateTime = LocalDateTime.of(2025, 12, 31, 23, 59, 59);
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("events")
                .groupBy("event_time")
                .having("event_time")
                .lte(testDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"event_time\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, testDateTime);
    }

    // NULL checks
    @Test
    void isNullCheck() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("email")
                .having("email")
                .isNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NULL");
    }

    @Test
    void isNotNullCheck() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("users")
                .groupBy("email")
                .having("email")
                .isNotNull()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"email\" IS NOT NULL");
    }

    // LIKE operator
    @Test
    void likeOperatorPattern() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having("name")
                .like("%electronics%")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "%electronics%");
    }

    @Test
    void likeOperatorStartsWith() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("name")
                .having("name")
                .like("Widget%")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING \"name\" LIKE ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "Widget%");
    }

    // IN operator
    @Test
    void inOperatorStrings() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("products")
                .groupBy("category")
                .having("category")
                .in("electronics", "books", "toys")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("status_id")
                .in(1, 2, 3, 4, 5)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("status")
                .in(true, false)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("event_date")
                .in(date1, date2, date3)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("event_time")
                .in(dt1, dt2)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("total")
                .between(100, 500)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING (\"total\" >= ?) AND (\"total\" <= ?");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 500);
    }

    @Test
    void betweenDecimalNumbers() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy("total")
                .having("total")
                .between(99.99, 499.99)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING (\"total\" >= ?) AND (\"total\" <= ?");
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
                .having("event_date")
                .between(startDate, endDate)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING (\"event_date\" >= ?) AND (\"event_date\" <= ?");
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
                .having("event_time")
                .between(startDateTime, endDateTime)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING (\"event_time\" >= ?) AND (\"event_time\" <= ?");
        ;
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
                .having("dept_id")
                .eq(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("dept_id")
                .ne(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("avg_salary")
                .gt(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("avg_salary")
                .lt(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("account_id")
                .gte(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("account_id")
                .lte(subquery)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("customer_id")
                .gt(100)
                .andHaving("customer_id")
                .lt(500)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("status")
                .eq("pending")
                .orHaving("status")
                .eq("cancelled")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING").contains("=").contains("OR");
    }

    // Complex combinations
    @Test
    void complexHavingWithMultipleConditions() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*), SUM(amount)")
                .from("orders")
                .groupBy("customer_id", "status")
                .having("customer_id")
                .gte(1)
                .andHaving("status")
                .in("active", "pending")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

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
                .having("event_date")
                .between(startDate, endDate)
                .andHaving("category")
                .in("A", "B", "C")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("HAVING")
                .contains(">=")
                .contains("<=")
                .contains("IN")
                .contains("AND");
    }
}
