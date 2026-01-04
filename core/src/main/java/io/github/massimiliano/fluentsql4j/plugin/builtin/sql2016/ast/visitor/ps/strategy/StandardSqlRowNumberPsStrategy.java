package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.RowNumber;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.RowNumberPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlRowNumberPsStrategy implements RowNumberPsStrategy {

    @Override
    public PreparedStatementSpec handle(RowNumber rowNumber, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ROW_NUMBER()");
        List<Object> parameters = new ArrayList<>();

        if (rowNumber.overClause() != null) {
            PreparedStatementSpec overResult = rowNumber.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
