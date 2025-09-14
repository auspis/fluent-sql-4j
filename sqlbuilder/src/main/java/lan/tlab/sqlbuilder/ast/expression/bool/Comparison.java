package lan.tlab.sqlbuilder.ast.expression.bool;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Comparison implements BooleanExpression {

    private final ScalarExpression lhs;
    private final ComparisonOperator operator;
    private final ScalarExpression rhs;

    @Getter
    @AllArgsConstructor
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
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
