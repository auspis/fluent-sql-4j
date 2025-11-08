package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.predicate.IsNull;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IsNullRenderStrategy;

public class StandardSqlIsNullRenderStrategy implements IsNullRenderStrategy {

    @Override
    public String render(IsNull expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("%s IS NULL", expression.expression().accept(sqlRenderer, ctx));
    }
}
