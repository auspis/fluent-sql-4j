package lan.tlab.sqlbuilder.ast.expression.scalar.convert;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Cast implements FunctionCall {

    private final ScalarExpression expression;
    private final String dataType;

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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
