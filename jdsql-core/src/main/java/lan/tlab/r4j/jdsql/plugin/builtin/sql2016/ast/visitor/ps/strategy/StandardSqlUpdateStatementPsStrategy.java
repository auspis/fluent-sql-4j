package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;

public class StandardSqlUpdateStatementPsStrategy implements UpdateStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(UpdateStatement stmt, PreparedStatementRenderer renderer, AstContext ctx) {
        // TableIdentifier name
        PreparedStatementSpec tableDto =
                stmt.table().accept(renderer, ctx); // Usa il visitor su qualunque TableExpression
        String tableName = tableDto.sql();

        List<UpdateItem> setItems = stmt.set();
        List<String> setClauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (UpdateItem item : setItems) {
            // Colonna
            PreparedStatementSpec colDto = item.column().accept(renderer, ctx);
            // Valore
            PreparedStatementSpec valDto = item.value().accept(renderer, ctx);
            setClauses.add(colDto.sql() + " = " + valDto.sql());
            params.addAll(valDto.parameters());
        }
        String setSql = String.join(", ", setClauses);

        // WHERE
        String whereSql = "";
        List<Object> whereParams = new ArrayList<>();
        if (stmt.where() != null) {
            PreparedStatementSpec whereDto = stmt.where().accept(renderer, ctx);
            if (whereDto.sql() != null && !whereDto.sql().isBlank()) {
                whereSql = " WHERE " + whereDto.sql();
                whereParams.addAll(whereDto.parameters());
            }
        }

        String sql = "UPDATE " + tableName + " SET " + setSql + whereSql;
        List<Object> allParams = new ArrayList<>(params);
        allParams.addAll(whereParams);
        return new PreparedStatementSpec(sql, allParams);
    }
}
