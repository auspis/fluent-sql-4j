package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;

public class DefaultUpdateStatementPsStrategy implements UpdateStatementPsStrategy {
    @Override
    public PsDto handle(UpdateStatement stmt, PreparedStatementVisitor visitor, AstContext ctx) {
        // TableIdentifier name
        PsDto tableDto = stmt.getTable().accept(visitor, ctx); // Usa il visitor su qualunque TableExpression
        String tableName = tableDto.sql();

        List<UpdateItem> setItems = stmt.getSet();
        List<String> setClauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (UpdateItem item : setItems) {
            // Colonna
            PsDto colDto = item.getColumn().accept(visitor, ctx);
            // Valore
            PsDto valDto = item.getValue().accept(visitor, ctx);
            setClauses.add(colDto.sql() + " = " + valDto.sql());
            params.addAll(valDto.parameters());
        }
        String setSql = String.join(", ", setClauses);

        // WHERE
        String whereSql = "";
        List<Object> whereParams = new ArrayList<>();
        if (stmt.getWhere() != null) {
            PsDto whereDto = stmt.getWhere().accept(visitor, ctx);
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
