package lan.tlab.r4j.jdsql.ast.core.predicate;

import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents a comparison predicate (e.g., =, >, <, >=, <=, !=).
 *
 * <p>Both left-hand side (lhs) and right-hand side (rhs) expressions are {@link ValueExpression}
 * which allows comparisons involving:
 * <ul>
 *   <li>Scalar expressions: column references, literals, functions, etc.
 *   <li>Aggregate expressions: aggregate functions like COUNT(*), SUM(), AVG(), etc.
 * </ul>
 *
 * <p>This is particularly useful in HAVING clauses where you compare aggregate results:
 * <pre>
 *   HAVING COUNT(*) > 10 OR SUM(amount) >= 100
 * </pre>
 *
 * <p>Note: Predicates and set expressions are NOT allowed as comparison operands because they
 * don't produce values.
 */
public record Comparison(ValueExpression lhs, ComparisonOperator operator, ValueExpression rhs) implements Predicate {

    public enum ComparisonOperator {
        EQUALS("="),
        NOT_EQUALS("!="),
        GREATER_THAN(">"),
        GREATER_THAN_OR_EQUALS(">="),
        LESS_THAN("<"),
        LESS_THAN_OR_EQUALS("<=");

        private final String sqlSymbol;

        ComparisonOperator(String sqlSymbol) {
            this.sqlSymbol = sqlSymbol;
        }

        public String getSqlSymbol() {
            return sqlSymbol;
        }
    }

    public static Comparison eq(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.EQUALS, rhs);
    }

    public static Comparison eq(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.EQUALS, rhs);
    }

    public static Comparison eq(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.EQUALS, rhs);
    }

    public static Comparison eq(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.EQUALS, rhs);
    }

    public static Comparison ne(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.NOT_EQUALS, rhs);
    }

    public static Comparison ne(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.NOT_EQUALS, rhs);
    }

    public static Comparison ne(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.NOT_EQUALS, rhs);
    }

    public static Comparison ne(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.NOT_EQUALS, rhs);
    }

    public static Comparison lt(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN, rhs);
    }

    public static Comparison lt(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN, rhs);
    }

    public static Comparison lt(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN, rhs);
    }

    public static Comparison lt(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN, rhs);
    }

    public static Comparison lte(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN_OR_EQUALS, rhs);
    }

    public static Comparison lte(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN_OR_EQUALS, rhs);
    }

    public static Comparison lte(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN_OR_EQUALS, rhs);
    }

    public static Comparison lte(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN_OR_EQUALS, rhs);
    }

    public static Comparison gt(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN, rhs);
    }

    public static Comparison gt(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN, rhs);
    }

    public static Comparison gt(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN, rhs);
    }

    public static Comparison gt(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN, rhs);
    }

    public static Comparison gte(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN_OR_EQUALS, rhs);
    }

    public static Comparison gte(AggregateExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN_OR_EQUALS, rhs);
    }

    public static Comparison gte(ScalarExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN_OR_EQUALS, rhs);
    }

    public static Comparison gte(AggregateExpression lhs, AggregateExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN_OR_EQUALS, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
