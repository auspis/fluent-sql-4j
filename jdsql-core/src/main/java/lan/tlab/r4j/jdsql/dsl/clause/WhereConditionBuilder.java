package lan.tlab.r4j.jdsql.dsl.clause;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import lan.tlab.r4j.jdsql.ast.core.expression.Expression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.In;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.Like;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;

/**
 * Generic builder for WHERE conditions that can work with any builder type.
 *
 * @param <T> the parent builder type
 */
public class WhereConditionBuilder<T extends SupportsWhere<T>> {
    private final T parent;
    private final String column;
    private final LogicalCombinator combinator;

    public WhereConditionBuilder(T parent, String column, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = column;
        this.combinator = combinator;
    }

    // String comparisons
    public T eq(String value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(String value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public T gt(String value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public T lt(String value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public T gte(String value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public T lte(String value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Number comparisons
    public T eq(Number value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(Number value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public T gt(Number value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public T lt(Number value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public T gte(Number value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public T lte(Number value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Boolean comparisons
    public T eq(Boolean value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(Boolean value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    // LocalDate comparisons
    public T eq(LocalDate value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(LocalDate value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public T gt(LocalDate value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public T lt(LocalDate value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public T gte(LocalDate value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public T lte(LocalDate value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // LocalDateTime comparisons
    public T eq(LocalDateTime value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(LocalDateTime value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public T gt(LocalDateTime value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public T lt(LocalDateTime value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public T gte(LocalDateTime value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public T lte(LocalDateTime value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // String-specific operations
    public T like(String pattern) {
        return addCondition(new Like(getColumnRef(), pattern));
    }

    // Null checks
    public T isNull() {
        return addCondition(new IsNull(getColumnRef()));
    }

    public T isNotNull() {
        return addCondition(new IsNotNull(getColumnRef()));
    }

    // Convenience methods for date ranges
    public T between(LocalDate startDate, LocalDate endDate) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(startDate)),
                Comparison.lte(getColumnRef(), Literal.of(endDate)));
        return addCondition(condition);
    }

    public T between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(startDateTime)),
                Comparison.lte(getColumnRef(), Literal.of(endDateTime)));
        return addCondition(condition);
    }

    public T between(Number min, Number max) {
        Predicate condition = AndOr.and(
                Comparison.gte(getColumnRef(), Literal.of(min)), Comparison.lte(getColumnRef(), Literal.of(max)));
        return addCondition(condition);
    }

    // Subquery comparisons
    public T eq(SelectBuilder subquery) {
        return addCondition(Comparison.eq(getColumnRef(), toScalarSubquery(subquery)));
    }

    public T ne(SelectBuilder subquery) {
        return addCondition(Comparison.ne(getColumnRef(), toScalarSubquery(subquery)));
    }

    public T gt(SelectBuilder subquery) {
        return addCondition(Comparison.gt(getColumnRef(), toScalarSubquery(subquery)));
    }

    public T lt(SelectBuilder subquery) {
        return addCondition(Comparison.lt(getColumnRef(), toScalarSubquery(subquery)));
    }

    public T gte(SelectBuilder subquery) {
        return addCondition(Comparison.gte(getColumnRef(), toScalarSubquery(subquery)));
    }

    public T lte(SelectBuilder subquery) {
        return addCondition(Comparison.lte(getColumnRef(), toScalarSubquery(subquery)));
    }

    // IN operator
    public T in(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public T in(Number... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public T in(Boolean... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public T in(LocalDate... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (Expression) Literal.of(v)).toList()));
    }

    public T in(LocalDateTime... values) {
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

    private T addCondition(Predicate condition) {
        return parent.addWhereCondition(condition, combinator);
    }
}
