package lan.tlab.sqlbuilder.ast.expression.scalar;

import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Builder;
import lombok.Getter;

public interface ArithmeticExpression extends ScalarExpression {

    public static BinaryArithmeticExpression addition(ScalarExpression lhs, ScalarExpression rhs) {
        return BinaryArithmeticExpression.builder()
                .lhs(lhs)
                .operator("+")
                .rhs(rhs)
                .build();
    }

    public static BinaryArithmeticExpression subtraction(ScalarExpression lhs, ScalarExpression rhs) {
        return BinaryArithmeticExpression.builder()
                .lhs(lhs)
                .operator("-")
                .rhs(rhs)
                .build();
    }

    public static BinaryArithmeticExpression multiplication(ScalarExpression lhs, ScalarExpression rhs) {
        return BinaryArithmeticExpression.builder()
                .lhs(lhs)
                .operator("*")
                .rhs(rhs)
                .build();
    }

    public static BinaryArithmeticExpression division(ScalarExpression lhs, ScalarExpression rhs) {
        return BinaryArithmeticExpression.builder()
                .lhs(lhs)
                .operator("/")
                .rhs(rhs)
                .build();
    }

    public static BinaryArithmeticExpression modulo(ScalarExpression lhs, ScalarExpression rhs) {
        return BinaryArithmeticExpression.builder()
                .lhs(lhs)
                .operator("%")
                .rhs(rhs)
                .build();
    }

    public static UnaryArithmeticExpression negation(ScalarExpression expression) {
        return UnaryArithmeticExpression.builder()
                .operator("-")
                .expression(expression)
                .build();
    }

    @Builder
    @Getter
    public static class BinaryArithmeticExpression implements ArithmeticExpression {

        private final ScalarExpression lhs;
        private final String operator;
        private final ScalarExpression rhs;

        @Override
        public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
            return visitor.visit(this);
        }
    }

    @Builder
    @Getter
    public static class UnaryArithmeticExpression implements ArithmeticExpression {

        private final String operator;
        private final ScalarExpression expression;

        @Override
        public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
            return visitor.visit(this);
        }
    }
}
