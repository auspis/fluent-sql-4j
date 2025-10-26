package lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
// TODO: evaluate if the record is the correct solution: the creation should go only through factory methods. see
// UnaryString.java

public record DateArithmetic(boolean add, ScalarExpression dateExpression, Interval interval) implements FunctionCall {

    public static DateArithmetic add(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(true, dateExpression, interval);
    }

    public static DateArithmetic subtract(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(false, dateExpression, interval);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
