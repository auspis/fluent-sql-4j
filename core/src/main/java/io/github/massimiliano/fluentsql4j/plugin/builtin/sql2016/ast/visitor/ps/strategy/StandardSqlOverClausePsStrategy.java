package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.window.OverClause;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.OverClausePsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardSqlOverClausePsStrategy implements OverClausePsStrategy {

    @Override
    public PreparedStatementSpec handle(OverClause overClause, Visitor<PreparedStatementSpec> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("OVER (");
        List<Object> parameters = new ArrayList<>();

        if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
            List<PreparedStatementSpec> partitionResults = overClause.partitionBy().stream()
                    .map(expr -> expr.accept(visitor, ctx))
                    .toList();

            sql.append("PARTITION BY ");
            sql.append(partitionResults.stream().map(PreparedStatementSpec::sql).collect(Collectors.joining(", ")));

            partitionResults.forEach(result -> parameters.addAll(result.parameters()));
        }

        if (overClause.orderBy() != null && !overClause.orderBy().isEmpty()) {
            if (overClause.partitionBy() != null && !overClause.partitionBy().isEmpty()) {
                sql.append(" ");
            }

            List<PreparedStatementSpec> orderResults = overClause.orderBy().stream()
                    .map(sort -> sort.accept(visitor, ctx))
                    .toList();

            sql.append("ORDER BY ");
            sql.append(orderResults.stream().map(PreparedStatementSpec::sql).collect(Collectors.joining(", ")));

            orderResults.forEach(result -> parameters.addAll(result.parameters()));
        }

        sql.append(")");
        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
