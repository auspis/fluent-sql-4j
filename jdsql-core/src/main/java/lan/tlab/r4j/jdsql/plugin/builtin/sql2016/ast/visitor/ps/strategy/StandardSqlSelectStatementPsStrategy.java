package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.SelectStatementPsStrategy;

public class StandardSqlSelectStatementPsStrategy implements SelectStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(SelectStatement stmt, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // SELECT ...
        PreparedStatementSpec selectResult = stmt.getSelect().accept(renderer, ctx);
        // FROM ...
        PreparedStatementSpec fromResult = stmt.getFrom().accept(renderer, ctx);
        // WHERE ... (optional)
        PreparedStatementSpec whereResult = null;
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
        PreparedStatementSpec groupByResult = null;
        String groupByClause = "";
        if (stmt.getGroupBy() != null
                && !stmt.getGroupBy().groupingExpressions().isEmpty()) {
            groupByResult = stmt.getGroupBy().accept(renderer, ctx);
            groupByClause = " GROUP BY " + groupByResult.sql();
        }
        // HAVING ... (optional, after GROUP BY)
        PreparedStatementSpec havingResult = null;
        String havingClause = "";
        if (stmt.getHaving() != null && stmt.getHaving().condition() != null) {
            havingResult = stmt.getHaving().accept(renderer, ctx);
            if (!havingResult.sql().isBlank()) {
                havingClause = " HAVING " + havingResult.sql();
            }
        }
        // ORDER BY ... (optional)
        PreparedStatementSpec orderByResult = null;
        String orderByClause = "";
        if (stmt.getOrderBy() != null && !stmt.getOrderBy().sortings().isEmpty()) {
            orderByResult = stmt.getOrderBy().accept(renderer, ctx);
            orderByClause = " ORDER BY " + orderByResult.sql();
        }
        // PAGINATION - delegate to proper pagination strategy
        PreparedStatementSpec paginationResult = null;
        String paginationClause = "";
        if (stmt.getFetch() != null && stmt.getFetch().isActive()) {
            paginationResult = stmt.getFetch().accept(renderer, ctx);
            paginationClause = " " + paginationResult.sql();
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
        return new PreparedStatementSpec(sql, allParams);
    }
}
