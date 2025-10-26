package lan.tlab.r4j.sql.ast.predicate;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Comparison(ScalarExpression lhs, ComparisonOperator operator, ScalarExpression rhs) implements Predicate {

    public enum ComparisonOperator {
        EQUALS("="),
        NOT_EQUALS("!="),
        // TODO: handle dialect
        // O "<>" a seconda del dialetto SQL
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

    public static Comparison ne(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.NOT_EQUALS, rhs);
    }

    public static Comparison lt(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN, rhs);
    }

    public static Comparison lte(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.LESS_THAN_OR_EQUALS, rhs);
    }

    public static Comparison gt(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN, rhs);
    }

    public static Comparison gte(ScalarExpression lhs, ScalarExpression rhs) {
        return new Comparison(lhs, ComparisonOperator.GREATER_THAN_OR_EQUALS, rhs);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
