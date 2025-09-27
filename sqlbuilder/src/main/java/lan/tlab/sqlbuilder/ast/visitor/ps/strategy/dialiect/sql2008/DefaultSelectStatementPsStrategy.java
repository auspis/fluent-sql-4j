package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.statement.SelectStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SelectStatementPsStrategy;

public class DefaultSelectStatementPsStrategy implements SelectStatementPsStrategy {
    @Override
    public PsDto handle(SelectStatement stmt, Visitor<PsDto> visitor, AstContext ctx) {
        // SELECT ...
        PsDto selectResult = stmt.getSelect().accept(visitor, ctx);
        // FROM ...
        PsDto fromResult = stmt.getFrom().accept(visitor, ctx);
        // WHERE ... (optional)
        PsDto whereResult = null;
        String whereClause = "";
        if (stmt.getWhere() != null && stmt.getWhere().getCondition() != null) {
            whereResult = stmt.getWhere().accept(visitor, ctx);
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
                && !stmt.getGroupBy().getGroupingExpressions().isEmpty()) {
            groupByResult = stmt.getGroupBy().accept(visitor, ctx);
            groupByClause = " GROUP BY " + groupByResult.sql();
        }
        // HAVING ... (optional, after GROUP BY)
        PsDto havingResult = null;
        String havingClause = "";
        if (stmt.getHaving() != null && stmt.getHaving().getCondition() != null) {
            havingResult = stmt.getHaving().accept(visitor, ctx);
            if (!havingResult.sql().isBlank()) {
                havingClause = " HAVING " + havingResult.sql();
            }
        }
        // ORDER BY ... (optional)
        PsDto orderByResult = null;
        String orderByClause = "";
        if (stmt.getOrderBy() != null && !stmt.getOrderBy().getSortings().isEmpty()) {
            orderByResult = stmt.getOrderBy().accept(visitor, ctx);
            orderByClause = " ORDER BY " + orderByResult.sql();
        }
        // PAGINATION - delegate to proper pagination strategy
        PsDto paginationResult = null;
        String paginationClause = "";
        if (stmt.getFetch() != null && stmt.getFetch().isActive()) {
            paginationResult = stmt.getFetch().accept(visitor, ctx);
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
