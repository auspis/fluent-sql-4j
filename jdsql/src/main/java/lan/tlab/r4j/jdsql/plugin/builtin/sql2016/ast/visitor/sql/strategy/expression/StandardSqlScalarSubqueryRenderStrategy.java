package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.ScalarSubqueryRenderStrategy;

public class StandardSqlScalarSubqueryRenderStrategy implements ScalarSubqueryRenderStrategy {

    @Override
    public String render(ScalarSubquery expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("(%s)", expression.tableExpression().accept(sqlRenderer, ctx));
    }
}
