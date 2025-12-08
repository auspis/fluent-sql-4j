package lan.tlab.r4j.jdsql.ast.core.expression.scalar;

import lan.tlab.r4j.jdsql.ast.core.expression.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Cast(ScalarExpression expression, String dataType) implements FunctionCall {

    // TODO: move to DSL
    // TODO: evaluate enum see UnaryString
    //    public static final String SQL_VARCHAR_255 = "VARCHAR(255)";
    //    public static final String SQL_NVARCHAR_50 = "NVARCHAR(50)";
    //    public static final String SQL_INT = "INT";
    //    public static final String SQL_BIGINT = "BIGINT";
    //    public static final String SQL_DECIMAL_10_2 = "DECIMAL(10,2)";
    //    public static final String SQL_DATE = "DATE";
    //    public static final String SQL_DATETIME = "DATETIME";
    //    public static final String SQL_TIMESTAMP = "TIMESTAMP";
    //    public static final String SQL_BOOLEAN = "BOOLEAN";
    //    public static final String SQL_TEXT = "TEXT";

    public static Cast of(ScalarExpression expression, String dataType) {
        return new Cast(expression, dataType);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
