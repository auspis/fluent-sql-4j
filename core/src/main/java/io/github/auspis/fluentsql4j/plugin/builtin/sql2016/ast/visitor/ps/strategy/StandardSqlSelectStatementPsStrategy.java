package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.SelectStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.dsl.helper.SelectClauseHelper;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlSelectStatementPsStrategy implements SelectStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(SelectStatement stmt, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // SELECT clause
        PreparedStatementSpec selectResult = stmt.getSelect().accept(renderer, ctx);
        sql.append("SELECT ").append(selectResult.sql());
        params.addAll(selectResult.parameters());

        // FROM clause
        PreparedStatementSpec fromResult = stmt.getFrom().accept(renderer, ctx);
        sql.append(" FROM ").append(fromResult.sql());
        params.addAll(fromResult.parameters());

        // Create helper for optional clauses
        SelectClauseHelper clauseHelper = new SelectClauseHelper(stmt);

        // WHERE clause (optional)
        if (clauseHelper.hasWhereClause()) {
            PreparedStatementSpec whereResult = stmt.getWhere().accept(renderer, ctx);
            clauseHelper.appendOptionalClause(sql, params, whereResult, "WHERE");
        }

        // GROUP BY clause (optional)
        if (clauseHelper.hasGroupByClause()) {
            PreparedStatementSpec groupByResult = stmt.getGroupBy().accept(renderer, ctx);
            clauseHelper.appendOptionalClause(sql, params, groupByResult, "GROUP BY");
        }

        // HAVING clause (optional, after GROUP BY)
        if (clauseHelper.hasHavingClause()) {
            PreparedStatementSpec havingResult = stmt.getHaving().accept(renderer, ctx);
            clauseHelper.appendOptionalClause(sql, params, havingResult, "HAVING");
        }

        // ORDER BY clause (optional)
        if (clauseHelper.hasOrderByClause()) {
            PreparedStatementSpec orderByResult = stmt.getOrderBy().accept(renderer, ctx);
            clauseHelper.appendOptionalClause(sql, params, orderByResult, "ORDER BY");
        }

        // PAGINATION/FETCH clause (optional)
        if (clauseHelper.hasPaginationClause()) {
            PreparedStatementSpec paginationResult = stmt.getFetch().accept(renderer, ctx);
            clauseHelper.appendClauseWithoutKeyword(sql, params, paginationResult);
        }

        return new PreparedStatementSpec(sql.toString(), params);
    }
}
