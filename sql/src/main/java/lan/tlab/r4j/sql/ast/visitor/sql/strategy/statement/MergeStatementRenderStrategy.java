package lan.tlab.r4j.sql.ast.visitor.sql.strategy.statement;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class MergeStatementRenderStrategy implements StatementRenderStrategy {

    public String render(MergeStatement statement, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("MERGE INTO ");
        sql.append(statement.getTargetTable().accept(sqlRenderer, ctx));

        if (!statement.getTargetAlias().getName().isEmpty()) {
            sql.append(" AS ").append(statement.getTargetAlias().accept(sqlRenderer, ctx));
        }

        sql.append(" USING ");
        sql.append(statement.getUsing().accept(sqlRenderer, ctx));

        sql.append(" ON ");
        sql.append(statement.getOnCondition().accept(sqlRenderer, ctx));

        String actions = statement.getActions().stream()
                .map(action -> action.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(" "));

        if (!actions.isEmpty()) {
            sql.append(" ").append(actions);
        }

        return sql.toString();
    }
}
