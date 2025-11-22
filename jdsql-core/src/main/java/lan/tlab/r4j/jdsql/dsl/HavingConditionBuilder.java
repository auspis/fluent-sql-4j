package lan.tlab.r4j.jdsql.dsl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.In;
import lan.tlab.r4j.jdsql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.common.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.common.predicate.Like;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;

/**
 * Builder for HAVING conditions in SELECT statements.
 */
public class HavingConditionBuilder {
    private final SelectBuilder parent;
    private final String column;
    private final LogicalCombinator combinator;

    public HavingConditionBuilder(SelectBuilder parent, String column, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = column;
        this.combinator = combinator;
    }

    // String comparisons
    public SelectBuilder eq(String value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(String value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gt(String value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lt(String value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gte(String value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lte(String value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Number comparisons
    public SelectBuilder eq(Number value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(Number value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gt(Number value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lt(Number value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gte(Number value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lte(Number value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Boolean comparisons
    public SelectBuilder eq(Boolean value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(Boolean value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    // LocalDate comparisons
    public SelectBuilder eq(LocalDate value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(LocalDate value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gt(LocalDate value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lt(LocalDate value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gte(LocalDate value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lte(LocalDate value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // LocalDateTime comparisons
    public SelectBuilder eq(LocalDateTime value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(LocalDateTime value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gt(LocalDateTime value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lt(LocalDateTime value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder gte(LocalDateTime value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder lte(LocalDateTime value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // String-specific operations
    public SelectBuilder like(String pattern) {
        return addCondition(new Like(getColumnRef(), pattern));
    }

    // Null checks
    public SelectBuilder isNull() {
        return addCondition(new IsNull(getColumnRef()));
    }

    public SelectBuilder isNotNull() {
        return addCondition(new IsNotNull(getColumnRef()));
    }

    // Convenience methods for date ranges
    public SelectBuilder between(LocalDate startDate, LocalDate endDate) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(startDate)),
                Comparison.lte(getColumnRef(), Literal.of(endDate)));
        return addCondition(condition);
    }

    public SelectBuilder between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(startDateTime)),
                Comparison.lte(getColumnRef(), Literal.of(endDateTime)));
        return addCondition(condition);
    }

    public SelectBuilder between(Number min, Number max) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(min)), Comparison.lte(getColumnRef(), Literal.of(max)));
        return addCondition(condition);
    }

    // Subquery comparisons
    public SelectBuilder eq(SelectBuilder subquery) {
        return addCondition(Comparison.eq(getColumnRef(), toScalarSubquery(subquery)));
    }

    public SelectBuilder ne(SelectBuilder subquery) {
        return addCondition(Comparison.ne(getColumnRef(), toScalarSubquery(subquery)));
    }

    public SelectBuilder gt(SelectBuilder subquery) {
        return addCondition(Comparison.gt(getColumnRef(), toScalarSubquery(subquery)));
    }

    public SelectBuilder lt(SelectBuilder subquery) {
        return addCondition(Comparison.lt(getColumnRef(), toScalarSubquery(subquery)));
    }

    public SelectBuilder gte(SelectBuilder subquery) {
        return addCondition(Comparison.gte(getColumnRef(), toScalarSubquery(subquery)));
    }

    public SelectBuilder lte(SelectBuilder subquery) {
        return addCondition(Comparison.lte(getColumnRef(), toScalarSubquery(subquery)));
    }

    // IN operator
    public SelectBuilder in(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(Number... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(Boolean... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(LocalDate... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(LocalDateTime... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    // Helper methods
    private ColumnReference getColumnRef() {
        return ColumnReference.of(parent.getTableReference(), column);
    }

    private ScalarSubquery toScalarSubquery(SelectBuilder subquery) {
        if (subquery == null) {
            throw new IllegalArgumentException("Subquery cannot be null");
        }
        return ScalarSubquery.builder()
                .tableExpression(subquery.getCurrentStatement())
                .build();
    }

    private SelectBuilder addCondition(Predicate condition) {
        return parent.addHavingCondition(condition, combinator);
    }
}
