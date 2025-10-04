package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.bool.logical.AndOr;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class AndOrRenderStrategy implements ExpressionRenderStrategy {

    public String render(AndOr expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return expression.getOperands().stream()
                .map(o -> String.format("(%s)", o.accept(sqlRenderer, ctx)))
                .collect(Collectors.joining(" " + expression.getOperator().name() + " "));
    }
}
