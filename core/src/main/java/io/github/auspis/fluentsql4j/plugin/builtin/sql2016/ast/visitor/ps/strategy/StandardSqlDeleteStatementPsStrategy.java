package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.dml.statement.DeleteStatement;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DeleteStatementPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlDeleteStatementPsStrategy implements DeleteStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(DeleteStatement stmt, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        TableExpression table = stmt.table();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Use renderer to properly escape table name
        PreparedStatementSpec tableDto = table.accept(renderer, ctx);
        sql.append("DELETE FROM ").append(tableDto.sql());
        params.addAll(tableDto.parameters());

        Where where = stmt.where();
        if (where != null
                && where.condition() != null
                && !(where.condition() instanceof io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate)) {
            PreparedStatementSpec whereDto = where.accept(renderer, ctx);
            sql.append(" WHERE ").append(whereDto.sql());
            params.addAll(whereDto.parameters());
        }
        return new PreparedStatementSpec(sql.toString(), params);
    }
}
