package lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record ExtractDatePart(FunctionName functionName, ScalarExpression dateExpression) implements FunctionCall {

    public enum FunctionName {
        YEAR,
        MONTH,
        DAY
    }

    public static ExtractDatePart year(ScalarExpression dateExpression) {
        return new ExtractDatePart(FunctionName.YEAR, dateExpression);
    }

    public static ExtractDatePart month(ScalarExpression dateExpression) {
        return new ExtractDatePart(FunctionName.MONTH, dateExpression);
    }

    public static ExtractDatePart day(ScalarExpression dateExpression) {
        return new ExtractDatePart(FunctionName.DAY, dateExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
