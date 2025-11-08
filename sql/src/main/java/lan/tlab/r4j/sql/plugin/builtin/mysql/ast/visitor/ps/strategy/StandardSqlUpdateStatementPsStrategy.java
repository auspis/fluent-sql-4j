package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.sql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;

public class StandardSqlUpdateStatementPsStrategy implements UpdateStatementPsStrategy {
    @Override
    public PsDto handle(UpdateStatement stmt, PreparedStatementRenderer renderer, AstContext ctx) {
        // TableIdentifier name
        PsDto tableDto = stmt.table().accept(renderer, ctx); // Usa il visitor su qualunque TableExpression
        String tableName = tableDto.sql();

        List<UpdateItem> setItems = stmt.set();
        List<String> setClauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (UpdateItem item : setItems) {
            // Colonna
            PsDto colDto = item.column().accept(renderer, ctx);
            // Valore
            PsDto valDto = item.value().accept(renderer, ctx);
            setClauses.add(colDto.sql() + " = " + valDto.sql());
            params.addAll(valDto.parameters());
        }
        String setSql = String.join(", ", setClauses);

        // WHERE
        String whereSql = "";
        List<Object> whereParams = new ArrayList<>();
        if (stmt.where() != null) {
            PsDto whereDto = stmt.where().accept(renderer, ctx);
            if (whereDto.sql() != null && !whereDto.sql().isBlank()) {
                whereSql = " WHERE " + whereDto.sql();
                whereParams.addAll(whereDto.parameters());
            }
        }

        String sql = "UPDATE " + tableName + " SET " + setSql + whereSql;
        List<Object> allParams = new ArrayList<>(params);
        allParams.addAll(whereParams);
        return new PsDto(sql, allParams);
    }
}
