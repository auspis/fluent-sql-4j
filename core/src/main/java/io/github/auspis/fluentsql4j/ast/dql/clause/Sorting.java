package io.github.auspis.fluentsql4j.ast.dql.clause;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents a sorting specification in an ORDER BY clause.
 *
 * <p>A sorting specification can be either:
 * <ul>
 *   <li>A {@link ScalarExpression}: column references, literals, functions, etc.
 *   <li>An {@link AggregateExpression}: aggregate functions like SUM(), AVG(), etc.
 * </ul>
 *
 * <p>The {@link #expression} is a {@link ValueExpression} which encompasses both scalar and
 * aggregate expressions. Predicates and set expressions are NOT allowed in ORDER BY.
 */
public record Sorting(ValueExpression expression, SortOrder sortOrder) implements Visitable {

    public enum SortOrder {
        ASC("ASC"),
        DESC("DESC"),
        DEFAULT("");

        private final String sqlKeyword;

        SortOrder(String sqlKeyword) {
            this.sqlKeyword = sqlKeyword;
        }

        public String getSqlKeyword() {
            return sqlKeyword;
        }
    }

    public static Sorting asc(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.ASC);
    }

    public static Sorting asc(AggregateExpression expression) {
        return new Sorting(expression, SortOrder.ASC);
    }

    public static Sorting desc(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.DESC);
    }

    public static Sorting desc(AggregateExpression expression) {
        return new Sorting(expression, SortOrder.DESC);
    }

    public static Sorting by(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.DEFAULT);
    }

    public static Sorting by(AggregateExpression expression) {
        return new Sorting(expression, SortOrder.DEFAULT);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
