package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public final class UnaryString implements FunctionCall {

    private final String functionName;
    private final ScalarExpression expression;

    private UnaryString(String functionName, ScalarExpression expression) {
        this.functionName = functionName;
        this.expression = expression;
    }

    public static UnaryString lower(ScalarExpression stringExpression) {
        return new UnaryString("LOWER", stringExpression);
    }

    public static UnaryString upper(ScalarExpression stringExpression) {
        return new UnaryString("UPPER", stringExpression);
    }

    public String functionName() {
        return functionName;
    }

    public ScalarExpression expression() {
        return expression;
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UnaryString that = (UnaryString) obj;
        return functionName.equals(that.functionName) && expression.equals(that.expression);
    }

    @Override
    public int hashCode() {
        return functionName.hashCode() * 31 + expression.hashCode();
    }

    @Override
    public String toString() {
        return "UnaryString[functionName=" + functionName + ", expression=" + expression + "]";
    }
}
