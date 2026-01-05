package io.github.auspis.fluentsql4j.ast.core.predicate;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BetweenTest {

    private SqlCaptureHelper sqlCaptureHelper;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        sqlCaptureHelper = new SqlCaptureHelper();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
    }

    @Test
    void betweenIntegerRange() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("price")
                .between(10, 100)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
    }

    @Test
    void betweenDecimalRange() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("orders")
                .where()
                .column("amount")
                .between(99.99, 999.99)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 99.99);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 999.99);
    }

    @Test
    void betweenNegativeRange() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("temperatures")
                .where()
                .column("celsius")
                .between(-10, 5)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, -10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 5);
    }

    @Test
    void betweenLongRange() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("transactions")
                .where()
                .column("id")
                .between(1000000L, 9999999L)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1000000L);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 9999999L);
    }

    @Test
    void betweenLocalDateRange() throws SQLException {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        new SelectBuilder(specFactory, "*")
                .from("events")
                .where()
                .column("event_date")
                .between(start, end)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, start);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, end);
    }

    @Test
    void betweenLocalDateTimeRange() throws SQLException {
        LocalDateTime start = LocalDateTime.of(2023, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2023, 6, 30, 23, 59);

        new SelectBuilder(specFactory, "*")
                .from("logs")
                .where()
                .column("timestamp")
                .between(start, end)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, start);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, end);
    }

    @Test
    void betweenWithAnd() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("price")
                .between(10, 100)
                .and()
                .column("stock")
                .gt(0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("AND").contains(">");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 0);
    }

    @Test
    void betweenWithOr() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("price")
                .between(10, 50)
                .or()
                .column("price")
                .between(100, 200)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN").contains("OR");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 50);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 200);
    }

    @Test
    void betweenInHavingClauseNumeric() throws SQLException {
        new SelectBuilder(specFactory, "COUNT(*)")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .having()
                .column("COUNT(*)")
                .between(5, 20)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING").contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 5);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 20);
    }

    @Test
    void betweenInHavingClauseDate() throws SQLException {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        new SelectBuilder(specFactory, "MIN(created_at)")
                .from("orders")
                .groupBy()
                .column("customer_id")
                .having()
                .column("MIN(created_at)")
                .between(start, end)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("HAVING").contains("BETWEEN").contains("AND");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, start);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, end);
    }

    @Test
    void multipleBetweenConditions() throws SQLException {
        new SelectBuilder(specFactory, "*")
                .from("products")
                .where()
                .column("price")
                .between(10, 100)
                .and()
                .column("weight")
                .between(0.5, 5.0)
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).contains("BETWEEN");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 10);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 0.5);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 5.0);
    }

    @Test
    void betweenPredicateConstruction() {
        ColumnReference column = ColumnReference.of("products", "price");
        Literal<Number> min = Literal.of(10);
        Literal<Number> max = Literal.of(100);

        Between between = new Between(column, min, max);

        assertThat(between.testExpression()).isEqualTo(column);
        assertThat(between.startExpression()).isEqualTo(min);
        assertThat(between.endExpression()).isEqualTo(max);
    }

    @Test
    void betweenPredicateWithDifferentTypes() {
        ColumnReference column = ColumnReference.of("orders", "amount");
        Literal<Number> min = Literal.of(99.99);
        Literal<Number> max = Literal.of(999.99);

        Between between = new Between(column, min, max);

        assertThat(between.testExpression()).isInstanceOf(ColumnReference.class);
        assertThat(between.startExpression()).isInstanceOf(Literal.class);
        assertThat(between.endExpression()).isInstanceOf(Literal.class);
    }

    @Test
    void betweenPredicateWithDateRange() {
        ColumnReference column = ColumnReference.of("events", "event_date");
        Literal<LocalDate> start = Literal.of(LocalDate.of(2023, 1, 1));
        Literal<LocalDate> end = Literal.of(LocalDate.of(2023, 12, 31));

        Between between = new Between(column, start, end);

        assertThat(between.testExpression()).isEqualTo(column);
        assertThat(between.startExpression()).isEqualTo(start);
        assertThat(between.endExpression()).isEqualTo(end);
    }
}
