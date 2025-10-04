package lan.tlab.r4j.sql.dsl.select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.expression.bool.BooleanExpression;
import lan.tlab.r4j.sql.ast.expression.bool.Comparison;
import lan.tlab.r4j.sql.ast.expression.bool.IsNotNull;
import lan.tlab.r4j.sql.ast.expression.bool.IsNull;
import lan.tlab.r4j.sql.ast.expression.bool.Like;
import lan.tlab.r4j.sql.ast.expression.bool.logical.AndOr;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;

public class WhereConditionBuilder {
    private final SelectBuilder parent;
    private final String column;
    private final LogicalCombinator combinator;

    public WhereConditionBuilder(SelectBuilder parent, String column, LogicalCombinator combinator) {
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
        return parent.updateWhere(where -> {
            BooleanExpression condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(startDate)),
                    Comparison.lte(getColumnRef(), Literal.of(endDate)));
            return SelectBuilder.combineConditions(where, condition, combinator);
        });
    }

    public SelectBuilder between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return parent.updateWhere(where -> {
            BooleanExpression condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(startDateTime)),
                    Comparison.lte(getColumnRef(), Literal.of(endDateTime)));
            return SelectBuilder.combineConditions(where, condition, combinator);
        });
    }

    public SelectBuilder between(Number min, Number max) {
        return parent.updateWhere(where -> {
            BooleanExpression condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(min)), Comparison.lte(getColumnRef(), Literal.of(max)));
            return SelectBuilder.combineConditions(where, condition, combinator);
        });
    }

    // Helper methods
    private ColumnReference getColumnRef() {
        return ColumnReference.of(parent.getTableReference(), column);
    }

    private SelectBuilder addCondition(BooleanExpression condition) {
        return parent.updateWhere(where -> SelectBuilder.combineConditions(where, condition, combinator));
    }
}
