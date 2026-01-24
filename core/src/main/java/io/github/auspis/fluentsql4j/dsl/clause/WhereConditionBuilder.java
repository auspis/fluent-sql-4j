package io.github.auspis.fluentsql4j.dsl.clause;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarSubquery;
import io.github.auspis.fluentsql4j.ast.core.predicate.Between;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.predicate.In;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNotNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.IsNull;
import io.github.auspis.fluentsql4j.ast.core.predicate.Like;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.dsl.select.SelectBuilder;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Generic builder for WHERE conditions that can work with any builder type.
 *
 * @param <T> the parent builder type
 */
public class WhereConditionBuilder<T extends SupportsWhere<T>> {
    private final T parent;
    private final String column;
    private final ColumnReference columnRef;
    private final LogicalCombinator combinator;

    /**
     * Constructor for single-table context (column name only).
     */
    public WhereConditionBuilder(T parent, String column, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = column;
        this.columnRef = null;
        this.combinator = combinator;
    }

    /**
     * Constructor for multi-table context (explicit ColumnReference).
     */
    public WhereConditionBuilder(T parent, ColumnReference columnRef, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = null;
        this.columnRef = columnRef;
        this.combinator = combinator;
    }

    // String comparisons - with value
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

    // String comparisons - column-to-column
    public ColumnComparator<T> eq() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.EQUALS);
    }

    public ColumnComparator<T> ne() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.NOT_EQUALS);
    }

    public ColumnComparator<T> gt() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.GREATER_THAN);
    }

    public ColumnComparator<T> lt() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.LESS_THAN);
    }

    public ColumnComparator<T> gte() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    public ColumnComparator<T> lte() {
        return new ColumnComparator<>(this, Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS);
    }

    // Number comparisons - with value
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

    // Boolean comparisons - with value
    public T eq(Boolean value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public T ne(Boolean value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    // LocalDate comparisons - with value
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

    // LocalDateTime comparisons - with value
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
        return addCondition(new Between(getColumnRef(), Literal.of(startDate), Literal.of(endDate)));
    }

    public T between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return addCondition(new Between(getColumnRef(), Literal.of(startDateTime), Literal.of(endDateTime)));
    }

    public T between(Number min, Number max) {
        return addCondition(new Between(getColumnRef(), Literal.of(min), Literal.of(max)));
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
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public T in(Number... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public T in(Boolean... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public T in(LocalDate... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public T in(LocalDateTime... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    // Helper methods
    private ColumnReference getColumnRef() {
        if (columnRef != null) {
            return columnRef; // Use explicit column reference (multi-table context)
        }
        return ColumnReferenceUtil.createWithTableReference(parent.getTableReference(), column);
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

    /**
     * Intermediate builder for column-to-column comparisons.
     * Allows fluent syntax: .where().column("u", "age").gt().column("z", "age")
     */
    public static class ColumnComparator<R extends SupportsWhere<R>> {
        private final WhereConditionBuilder<R> parent;
        private final Comparison.ComparisonOperator operator;

        public ColumnComparator(WhereConditionBuilder<R> parent, Comparison.ComparisonOperator operator) {
            this.parent = parent;
            this.operator = operator;
        }

        /**
         * Compare with a column from another table.
         */
        public R column(String alias, String column) {
            if (alias == null || alias.trim().isEmpty()) {
                throw new IllegalArgumentException("Alias cannot be null or empty");
            }
            if (column == null || column.trim().isEmpty()) {
                throw new IllegalArgumentException("Column cannot be null or empty");
            }

            String trimmedAlias = alias.trim();
            String trimmedColumn = column.trim();

            if (trimmedAlias.contains(".")) {
                throw new IllegalArgumentException("Alias must not contain dot");
            }
            if (trimmedColumn.contains(".")) {
                throw new IllegalArgumentException("Column must not contain dot");
            }

            ColumnReference rightColumn = ColumnReference.of(trimmedAlias, trimmedColumn);
            Predicate condition =
                    switch (operator) {
                        case EQUALS -> Comparison.eq(parent.getColumnRef(), rightColumn);
                        case NOT_EQUALS -> Comparison.ne(parent.getColumnRef(), rightColumn);
                        case GREATER_THAN -> Comparison.gt(parent.getColumnRef(), rightColumn);
                        case LESS_THAN -> Comparison.lt(parent.getColumnRef(), rightColumn);
                        case GREATER_THAN_OR_EQUALS -> Comparison.gte(parent.getColumnRef(), rightColumn);
                        case LESS_THAN_OR_EQUALS -> Comparison.lte(parent.getColumnRef(), rightColumn);
                    };

            return parent.addCondition(condition);
        }
    }
}
