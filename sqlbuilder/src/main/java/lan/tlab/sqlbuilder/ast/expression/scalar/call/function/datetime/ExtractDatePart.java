package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ExtractDatePart implements FunctionCall {

    private final String functionName;
    protected final ScalarExpression dateExpression;

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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
