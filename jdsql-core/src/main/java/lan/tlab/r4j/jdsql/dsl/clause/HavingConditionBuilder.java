package lan.tlab.r4j.jdsql.dsl.clause;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.core.predicate.Between;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.In;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.core.predicate.Like;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;

/**
 * Builder for HAVING conditions in SELECT statements.
 */
public class HavingConditionBuilder {
    private final SelectBuilder parent;
    private final String column;
    private final ColumnReference columnRef;
    private final LogicalCombinator combinator;

    /**
     * Constructor for single-table context (column name only).
     */
    public HavingConditionBuilder(SelectBuilder parent, String column, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = column;
        this.columnRef = null;
        this.combinator = combinator;
    }

    /**
     * Constructor for multi-table context (explicit ColumnReference).
     */
    public HavingConditionBuilder(SelectBuilder parent, ColumnReference columnRef, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = null;
        this.columnRef = columnRef;
        this.combinator = combinator;
    }

    // String comparisons - with value
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

    // String comparisons - column-to-column
    public ColumnComparator eq() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.EQUALS);
    }

    public ColumnComparator ne() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.NOT_EQUALS);
    }

    public ColumnComparator gt() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.GREATER_THAN);
    }

    public ColumnComparator lt() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.LESS_THAN);
    }

    public ColumnComparator gte() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.GREATER_THAN_OR_EQUALS);
    }

    public ColumnComparator lte() {
        return new ColumnComparator(this, Comparison.ComparisonOperator.LESS_THAN_OR_EQUALS);
    }

    // Number comparisons - with value
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

    // Boolean comparisons - with value
    public SelectBuilder eq(Boolean value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public SelectBuilder ne(Boolean value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    // LocalDate comparisons - with value
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

    // LocalDateTime comparisons - with value
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
        return addCondition(new Between(getColumnRef(), Literal.of(startDate), Literal.of(endDate)));
    }

    public SelectBuilder between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return addCondition(new Between(getColumnRef(), Literal.of(startDateTime), Literal.of(endDateTime)));
    }

    public SelectBuilder between(Number min, Number max) {
        return addCondition(new Between(getColumnRef(), Literal.of(min), Literal.of(max)));
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
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(Number... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(Boolean... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(LocalDate... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided for IN clause");
        }
        return addCondition(new In(
                getColumnRef(),
                Arrays.stream(values).map(v -> (ValueExpression) Literal.of(v)).toList()));
    }

    public SelectBuilder in(LocalDateTime... values) {
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

    /**
     * Intermediate builder for column-to-column comparisons in HAVING clause.
     * Allows fluent syntax: .having().column("o", "total").gt().column("c", "total")
     */
    public class ColumnComparator {
        private final HavingConditionBuilder parent;
        private final Comparison.ComparisonOperator operator;

        public ColumnComparator(HavingConditionBuilder parent, Comparison.ComparisonOperator operator) {
            this.parent = parent;
            this.operator = operator;
        }

        /**
         * Compare with a column from another table.
         */
        public SelectBuilder column(String alias, String column) {
            if (alias == null || alias.isEmpty()) {
                throw new IllegalArgumentException("Alias cannot be null or empty");
            }
            if (alias.contains(".")) {
                throw new IllegalArgumentException("Alias must not contain dot");
            }
            if (column == null || column.isEmpty()) {
                throw new IllegalArgumentException("Column cannot be null or empty");
            }
            if (column.contains(".")) {
                throw new IllegalArgumentException("Column must not contain dot");
            }

            ColumnReference rightColumn = ColumnReference.of(alias, column);
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
