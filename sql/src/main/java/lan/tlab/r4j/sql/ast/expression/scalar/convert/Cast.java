package lan.tlab.r4j.sql.ast.expression.scalar.convert;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Cast(ScalarExpression expression, String dataType) implements FunctionCall {

    // TODO: move to DSL
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

    public static Cast sqlServer(ScalarExpression expression, String dataType) {
        return new Cast(expression, dataType);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
