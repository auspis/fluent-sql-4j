package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.statement.UpdateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateStatementPsStrategy;

public class StandardSqlUpdateStatementPsStrategy implements UpdateStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            UpdateStatement stmt, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        // TableIdentifier name
        PreparedStatementSpec tableDto =
                stmt.table().accept(astToPsSpecVisitor, ctx); // Usa il visitor su qualunque TableExpression
        String tableName = tableDto.sql();

        List<UpdateItem> setItems = stmt.set();
        List<String> setClauses = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (UpdateItem item : setItems) {
            // Colonna
            PreparedStatementSpec colDto = item.column().accept(astToPsSpecVisitor, ctx);
            // Valore
            PreparedStatementSpec valDto = item.value().accept(astToPsSpecVisitor, ctx);
            setClauses.add(colDto.sql() + " = " + valDto.sql());
            params.addAll(valDto.parameters());
        }
        String setSql = String.join(", ", setClauses);

        // WHERE
        String whereSql = "";
        List<Object> whereParams = new ArrayList<>();
        if (stmt.where() != null) {
            PreparedStatementSpec whereDto = stmt.where().accept(astToPsSpecVisitor, ctx);
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
