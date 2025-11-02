package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.AndOrRenderStrategy;

public class StandardSqlAndOrRenderStrategy implements AndOrRenderStrategy {

    @Override
    public String render(AndOr expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return expression.operands().stream()
                .map(o -> String.format("(%s)", o.accept(sqlRenderer, ctx)))
                .collect(Collectors.joining(" " + expression.operator().name() + " "));
    }
}
