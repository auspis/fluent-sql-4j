package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Trim implements FunctionCall {

    public enum TrimMode {
        BOTH,
        LEADING,
        TRAILING
    }

    private final ScalarExpression stringExpression;
    private final TrimMode mode;
    private final ScalarExpression charactersToRemove;

    public static Trim trim(ScalarExpression stringExpression) {
        return new Trim(stringExpression, null, null);
    }

    public static Trim trimBoth(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.BOTH, null);
    }

    public static Trim trimLeading(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.LEADING, null);
    }

    public static Trim trimTrailing(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.TRAILING, null);
    }

    public static Trim trim(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, null, charactersToRemove);
    }

    public static Trim trimBoth(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.BOTH, charactersToRemove);
    }

    public static Trim trimLeading(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.LEADING, charactersToRemove);
    }

    public static Trim trimTrailing(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.TRAILING, charactersToRemove);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
