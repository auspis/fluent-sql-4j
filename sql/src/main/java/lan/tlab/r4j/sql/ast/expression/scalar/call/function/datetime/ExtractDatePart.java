package lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Getter;

@Getter
public class ExtractDatePart implements FunctionCall {

    private final String functionName;
    protected final ScalarExpression dateExpression;

    ExtractDatePart(String functionName, ScalarExpression dateExpression) {
        this.functionName = functionName;
        this.dateExpression = dateExpression;
    }

    public static ExtractDatePart year(ScalarExpression dateExpression) {
        return new ExtractDatePart("YEAR", dateExpression);
    }

    public static ExtractDatePart month(ScalarExpression dateExpression) {
        return new ExtractDatePart("MONTH", dateExpression);
    }

    public static ExtractDatePart day(ScalarExpression dateExpression) {
        return new ExtractDatePart("DAY", dateExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
