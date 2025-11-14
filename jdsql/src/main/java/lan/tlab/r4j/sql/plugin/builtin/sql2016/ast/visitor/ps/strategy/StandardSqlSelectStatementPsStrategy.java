package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SelectStatementPsStrategy;

public class StandardSqlSelectStatementPsStrategy implements SelectStatementPsStrategy {
    @Override
    public PsDto handle(SelectStatement stmt, Visitor<PsDto> renderer, AstContext ctx) {
        // SELECT ...
        PsDto selectResult = stmt.getSelect().accept(renderer, ctx);
        // FROM ...
        PsDto fromResult = stmt.getFrom().accept(renderer, ctx);
        // WHERE ... (optional)
        PsDto whereResult = null;
        String whereClause = "";
        if (stmt.getWhere() != null && stmt.getWhere().condition() != null) {
            whereResult = stmt.getWhere().accept(renderer, ctx);
            String whereSql = whereResult.sql();
            if (!whereSql.isBlank()) {
                // If the whereSql already starts with WHERE, don't prepend it
                if (whereSql.trim().toUpperCase().startsWith("WHERE")) {
                    whereClause = " " + whereSql.trim();
                } else {
                    whereClause = " WHERE " + whereSql;
                }
            }
        }
        // GROUP BY ... (optional)
        PsDto groupByResult = null;
        String groupByClause = "";
        if (stmt.getGroupBy() != null
                && !stmt.getGroupBy().groupingExpressions().isEmpty()) {
            groupByResult = stmt.getGroupBy().accept(renderer, ctx);
            groupByClause = " GROUP BY " + groupByResult.sql();
        }
        // HAVING ... (optional, after GROUP BY)
        PsDto havingResult = null;
        String havingClause = "";
        if (stmt.getHaving() != null && stmt.getHaving().condition() != null) {
            havingResult = stmt.getHaving().accept(renderer, ctx);
            if (!havingResult.sql().isBlank()) {
                havingClause = " HAVING " + havingResult.sql();
            }
        }
        // ORDER BY ... (optional)
        PsDto orderByResult = null;
        String orderByClause = "";
        if (stmt.getOrderBy() != null && !stmt.getOrderBy().sortings().isEmpty()) {
            orderByResult = stmt.getOrderBy().accept(renderer, ctx);
            orderByClause = " ORDER BY " + orderByResult.sql();
        }
        // PAGINATION - delegate to proper pagination strategy
        PsDto paginationResult = null;
        String paginationClause = "";
        if (stmt.getFetch() != null && stmt.getFetch().isActive()) {
            paginationResult = stmt.getFetch().accept(renderer, ctx);
            paginationClause = paginationResult.sql();
        }
        String sql = "SELECT " + selectResult.sql() + " FROM " + fromResult.sql() + whereClause + groupByClause
                + havingClause + orderByClause + paginationClause;
        List<Object> allParams = new ArrayList<>();
        allParams.addAll(selectResult.parameters());
        allParams.addAll(fromResult.parameters());
        if (whereResult != null) {
            allParams.addAll(whereResult.parameters());
        }
        if (groupByResult != null) {
            allParams.addAll(groupByResult.parameters());
        }
        if (havingResult != null) {
            allParams.addAll(havingResult.parameters());
        }
        if (orderByResult != null) {
            allParams.addAll(orderByResult.parameters());
        }
        if (paginationResult != null) {
            allParams.addAll(paginationResult.parameters());
        }
        return new PsDto(sql, allParams);
    }
}
