package lan.tlab.sqlbuilder.ast.clause.conditional.where;

import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Where implements Clause {

    @Default
    private final BooleanExpression condition = new NullBooleanExpression();

    public static Where andOf(BooleanExpression... conditions) {
        return builder().condition(AndOr.and(conditions)).build();
    }

    public static Where of(BooleanExpression condition) {
        return builder().condition(condition).build();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
