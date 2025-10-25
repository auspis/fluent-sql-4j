package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class MergeUsingRenderStrategy {

    public String render(MergeUsing using, SqlRenderer sqlRenderer, AstContext ctx) {
        return switch (using) {
            case MergeUsing.TableSource tableSource -> tableSource.table().accept(sqlRenderer, ctx);
            case MergeUsing.SubquerySource subquerySource -> {
                StringBuilder sql = new StringBuilder();
                sql.append("(")
                        .append(subquerySource.subquery().accept(sqlRenderer, ctx))
                        .append(")");
                String alias = subquerySource.alias().accept(sqlRenderer, ctx);
                if (!alias.isEmpty()) {
                    sql.append(" ").append(alias);
                }
                yield sql.toString();
            }
        };
    }
}
