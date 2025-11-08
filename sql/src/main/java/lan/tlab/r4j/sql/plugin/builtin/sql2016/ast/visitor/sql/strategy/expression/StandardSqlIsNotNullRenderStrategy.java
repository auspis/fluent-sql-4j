package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.predicate.IsNotNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IsNotNullRenderStrategy;

public class StandardSqlIsNotNullRenderStrategy implements IsNotNullRenderStrategy {

    @Override
    public String render(IsNotNull expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s IS NOT NULL", expression.expression().accept(sqlRenderer, ctx));
    }
}
