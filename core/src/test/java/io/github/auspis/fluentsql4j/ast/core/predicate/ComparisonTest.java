package io.github.auspis.fluentsql4j.ast.core.predicate;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ComparisonTest {

    @Test
    void eqWithLiterals() {
        Comparison comp = Comparison.eq(Literal.of("John"), Literal.of("John"));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void neWithLiterals() {
        Comparison comp = Comparison.ne(Literal.of("John"), Literal.of("Jane"));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void gtWithNumbers() {
        Comparison comp = Comparison.gt(Literal.of(100), Literal.of(50));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN);
    }

    @Test
    void ltWithNumbers() {
        Comparison comp = Comparison.lt(Literal.of(25), Literal.of(100));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN);
    }

    @Test
    void gteWithNumbers() {
        Comparison comp = Comparison.gte(Literal.of(100), Literal.of(100));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    @Test
    void lteWithNumbers() {
        Comparison comp = Comparison.lte(Literal.of(50), Literal.of(100));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS);
    }

    @Test
    void eqWithColumnReference() {
        Comparison comp = Comparison.eq(ColumnReference.of("users", "age"), Literal.of(30));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void neWithColumnReference() {
        Comparison comp = Comparison.ne(ColumnReference.of("users", "status"), Literal.of("inactive"));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void gtWithColumnReferenceDates() {
        Comparison comp = Comparison.gt(ColumnReference.of("orders", "created"), Literal.of(LocalDate.of(2024, 1, 1)));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN);
    }

    @Test
    void ltWithColumnReferenceDates() {
        Comparison comp =
                Comparison.lt(ColumnReference.of("orders", "updated"), Literal.of(LocalDate.of(2024, 12, 31)));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN);
    }

    @Test
    void gteWithColumnReferenceDateTimes() {
        Comparison comp = Comparison.gte(
                ColumnReference.of("events", "timestamp"), Literal.of(LocalDateTime.of(2024, 1, 1, 0, 0, 0)));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    @Test
    void lteWithColumnReferenceDateTimes() {
        Comparison comp = Comparison.lte(
                ColumnReference.of("events", "end_time"), Literal.of(LocalDateTime.of(2024, 12, 31, 23, 59, 59)));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS);
    }

    @Test
    void eqWithBooleanLiterals() {
        Comparison comp = Comparison.eq(Literal.of(true), Literal.of(true));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void neWithBooleanLiterals() {
        Comparison comp = Comparison.ne(Literal.of(true), Literal.of(false));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void eqWithNullLiteral() {
        Comparison comp = Comparison.eq(Literal.of("value"), Literal.ofNull());

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void neWithNullLiteral() {
        Comparison comp = Comparison.ne(ColumnReference.of("table", "column"), Literal.ofNull());

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void columnReferenceToColumnReference() {
        Comparison comp = Comparison.eq(ColumnReference.of("table1", "col1"), ColumnReference.of("table2", "col2"));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void gtWithDecimalNumbers() {
        Comparison comp = Comparison.gt(Literal.of(3.14159), Literal.of(2.71828));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN);
    }

    @Test
    void ltWithNegativeNumbers() {
        Comparison comp = Comparison.lt(Literal.of(-100), Literal.of(0));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN);
    }

    @Test
    void eqWithEmptyStrings() {
        Comparison comp = Comparison.eq(Literal.of(""), Literal.of(""));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void neWithStringContainingSpecialCharacters() {
        Comparison comp = Comparison.ne(Literal.of("it's@email.com"), Literal.of("test@example.com"));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void comparisonOperatorEqualsSymbol() {
        assertThat(Comparison.ComparisonOperator.EQUALS.getSqlSymbol()).isEqualTo("=");
    }

    @Test
    void comparisonOperatorNotEqualsSymbol() {
        assertThat(Comparison.ComparisonOperator.NOT_EQUALS.getSqlSymbol()).isEqualTo("!=");
    }

    @Test
    void comparisonOperatorGreaterThanSymbol() {
        assertThat(Comparison.ComparisonOperator.GREATER_THAN.getSqlSymbol()).isEqualTo(">");
    }

    @Test
    void comparisonOperatorLessThanSymbol() {
        assertThat(Comparison.ComparisonOperator.LESS_THAN.getSqlSymbol()).isEqualTo("<");
    }

    @Test
    void comparisonOperatorGreaterThanOrEqualsSymbol() {
        assertThat(Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS.getSqlSymbol())
                .isEqualTo(">=");
    }

    @Test
    void comparisonOperatorLessThanOrEqualsSymbol() {
        assertThat(Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS.getSqlSymbol())
                .isEqualTo("<=");
    }

    // Aggregate function tests
    @Test
    void countAggregateComparison() {
        Comparison comp = Comparison.eq(Literal.of("COUNT(*)"), Literal.of(10));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }

    @Test
    void sumAggregateComparison() {
        Comparison comp = Comparison.gt(Literal.of("SUM(amount)"), Literal.of(1000));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN);
    }

    @Test
    void avgAggregateComparison() {
        Comparison comp = Comparison.gte(Literal.of("AVG(salary)"), Literal.of(50000));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    @Test
    void maxAggregateComparison() {
        Comparison comp = Comparison.lt(Literal.of("MAX(price)"), Literal.of(1000));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN);
    }

    @Test
    void minAggregateComparison() {
        Comparison comp = Comparison.lte(Literal.of("MIN(value)"), Literal.of(100));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS);
    }

    @Test
    void countDistinctAggregateComparison() {
        Comparison comp = Comparison.ne(Literal.of("COUNT(DISTINCT category)"), Literal.of(0));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.NOT_EQUALS);
    }

    @Test
    void multipleAggregateCombination() {
        Comparison comp = Comparison.gte(Literal.of("SUM(orders)"), Literal.of(100));

        assertThat(comp).isNotNull();
        assertThat(comp.lhs()).isNotNull();
        assertThat(comp.rhs()).isNotNull();
    }

    @Test
    void aggregateWithColumnReference() {
        Comparison comp = Comparison.gt(ColumnReference.of("summary", "total_count"), Literal.of(5));

        assertThat(comp).isNotNull();
        assertThat(comp.lhs()).isNotNull();
    }

    @Test
    void aggregateComparisonWithDecimal() {
        Comparison comp = Comparison.gte(Literal.of("AVG(rating)"), Literal.of(3.5));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    @Test
    void complexAggregateExpression() {
        Comparison comp = Comparison.eq(Literal.of("SUM(CASE WHEN status='active' THEN 1 ELSE 0 END)"), Literal.of(42));

        assertThat(comp).isNotNull();
        assertThat(comp.operator()).isEqualTo(Comparison.ComparisonOperator.EQUALS);
    }
}
