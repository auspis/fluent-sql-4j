package lan.tlab.r4j.sql.dsl.delete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.predicate.IsNull;
import lan.tlab.r4j.sql.ast.predicate.Like;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;

public class WhereConditionBuilder {
    private final DeleteBuilder parent;
    private final String column;
    private final LogicalCombinator combinator;

    public WhereConditionBuilder(DeleteBuilder parent, String column, LogicalCombinator combinator) {
        this.parent = parent;
        this.column = column;
        this.combinator = combinator;
    }

    // String comparisons
    public DeleteBuilder eq(String value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder ne(String value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gt(String value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lt(String value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gte(String value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lte(String value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Number comparisons
    public DeleteBuilder eq(Number value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder ne(Number value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gt(Number value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lt(Number value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gte(Number value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lte(Number value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // Boolean comparisons
    public DeleteBuilder eq(Boolean value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder ne(Boolean value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    // LocalDate comparisons
    public DeleteBuilder eq(LocalDate value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder ne(LocalDate value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gt(LocalDate value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lt(LocalDate value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gte(LocalDate value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lte(LocalDate value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // LocalDateTime comparisons
    public DeleteBuilder eq(LocalDateTime value) {
        return addCondition(Comparison.eq(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder ne(LocalDateTime value) {
        return addCondition(Comparison.ne(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gt(LocalDateTime value) {
        return addCondition(Comparison.gt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lt(LocalDateTime value) {
        return addCondition(Comparison.lt(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder gte(LocalDateTime value) {
        return addCondition(Comparison.gte(getColumnRef(), Literal.of(value)));
    }

    public DeleteBuilder lte(LocalDateTime value) {
        return addCondition(Comparison.lte(getColumnRef(), Literal.of(value)));
    }

    // String-specific operations
    public DeleteBuilder like(String pattern) {
        return addCondition(new Like(getColumnRef(), pattern));
    }

    // Null checks
    public DeleteBuilder isNull() {
        return addCondition(new IsNull(getColumnRef()));
    }

    public DeleteBuilder isNotNull() {
        return addCondition(new IsNotNull(getColumnRef()));
    }

    // Convenience methods for date ranges
    public DeleteBuilder between(LocalDate startDate, LocalDate endDate) {
        return parent.updateWhere(where -> {
            Predicate condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(startDate)),
                    Comparison.lte(getColumnRef(), Literal.of(endDate)));
            return DeleteBuilder.combineConditions(where, condition, combinator);
        });
    }

    public DeleteBuilder between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return parent.updateWhere(where -> {
            Predicate condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(startDateTime)),
                    Comparison.lte(getColumnRef(), Literal.of(endDateTime)));
            return DeleteBuilder.combineConditions(where, condition, combinator);
        });
    }

    public DeleteBuilder between(Number min, Number max) {
        return parent.updateWhere(where -> {
            Predicate condition = AndOr.and(
                    Comparison.gte(getColumnRef(), Literal.of(min)), Comparison.lte(getColumnRef(), Literal.of(max)));
            return DeleteBuilder.combineConditions(where, condition, combinator);
        });
    }

    // Helper methods
    private ColumnReference getColumnRef() {
        return ColumnReference.of(parent.getTableReference(), column);
    }

    private DeleteBuilder addCondition(Predicate condition) {
        return parent.updateWhere(where -> DeleteBuilder.combineConditions(where, condition, combinator));
    }
}
