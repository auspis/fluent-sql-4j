package lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public final class DateArithmetic implements FunctionCall {

    private final boolean add;
    private final ScalarExpression dateExpression;
    private final Interval interval;

    private DateArithmetic(boolean add, ScalarExpression dateExpression, Interval interval) {
        this.add = add;
        this.dateExpression = dateExpression;
        this.interval = interval;
    }

    public static DateArithmetic add(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(true, dateExpression, interval);
    }

    public static DateArithmetic subtract(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(false, dateExpression, interval);
    }

    public boolean add() {
        return add;
    }

    public ScalarExpression dateExpression() {
        return dateExpression;
    }

    public Interval interval() {
        return interval;
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DateArithmetic that = (DateArithmetic) obj;
        return add == that.add && dateExpression.equals(that.dateExpression) && interval.equals(that.interval);
    }

    @Override
    public int hashCode() {
        int result = Boolean.hashCode(add);
        result = result * 31 + dateExpression.hashCode();
        result = result * 31 + interval.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DateArithmetic[add=" + add + ", dateExpression=" + dateExpression + ", interval=" + interval + "]";
    }
}
