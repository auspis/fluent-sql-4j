package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.logical.Not;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.NotRenderStrategy;

public class StandardSqlNotRenderStrategy implements NotRenderStrategy {

    @Override
    public String render(Not expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("NOT (%s)", expression.expression().accept(sqlRenderer, ctx));
    }
}
