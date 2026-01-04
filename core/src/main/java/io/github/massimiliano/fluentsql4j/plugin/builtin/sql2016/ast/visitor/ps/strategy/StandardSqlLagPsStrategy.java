package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.Lag;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.LagPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlLagPsStrategy implements LagPsStrategy {

    @Override
    public PreparedStatementSpec handle(Lag lag, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        List<Object> parameters = new ArrayList<>();
        PreparedStatementSpec exprResult = lag.expression().accept(visitor, ctx);
        parameters.addAll(exprResult.parameters());

        StringBuilder sql =
                new StringBuilder("LAG(").append(exprResult.sql()).append(", ").append(lag.offset());

        if (lag.defaultValue() != null) {
            PreparedStatementSpec defaultResult = lag.defaultValue().accept(visitor, ctx);
            sql.append(", ").append(defaultResult.sql());
            parameters.addAll(defaultResult.parameters());
        }

        sql.append(")");

        if (lag.overClause() != null) {
            PreparedStatementSpec overResult = lag.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
