package lan.tlab.r4j.jdsql.ast.core.expression.scalar;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public interface ArithmeticExpression extends ScalarExpression {

    public static BinaryArithmeticExpression addition(ScalarExpression lhs, ScalarExpression rhs) {
        return new BinaryArithmeticExpression(lhs, "+", rhs);
    }

    public static BinaryArithmeticExpression subtraction(ScalarExpression lhs, ScalarExpression rhs) {
        return new BinaryArithmeticExpression(lhs, "-", rhs);
    }

    public static BinaryArithmeticExpression multiplication(ScalarExpression lhs, ScalarExpression rhs) {
        return new BinaryArithmeticExpression(lhs, "*", rhs);
    }

    public static BinaryArithmeticExpression division(ScalarExpression lhs, ScalarExpression rhs) {
        return new BinaryArithmeticExpression(lhs, "/", rhs);
    }

    public static BinaryArithmeticExpression modulo(ScalarExpression lhs, ScalarExpression rhs) {
        return new BinaryArithmeticExpression(lhs, "%", rhs);
    }

    public static UnaryArithmeticExpression negation(ScalarExpression expression) {
        return new UnaryArithmeticExpression("-", expression);
    }

    public static record BinaryArithmeticExpression(ScalarExpression lhs, String operator, ScalarExpression rhs)
            implements ArithmeticExpression {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record UnaryArithmeticExpression(String operator, ScalarExpression expression)
            implements ArithmeticExpression {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
