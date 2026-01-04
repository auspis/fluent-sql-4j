package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.Select;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.Projection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.SelectClausePsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StandardSqlSelectClausePsStrategy implements SelectClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Select select, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<Projection> projections = select.projections();
        if (projections.isEmpty()) {
            return new PreparedStatementSpec("*", List.of());
        }

        List<Object> allParameters = new ArrayList<>();
        String sql = projections.stream()
                .map(p -> {
                    PreparedStatementSpec result = p.accept(renderer, ctx);
                    allParameters.addAll(result.parameters());
                    return result.sql();
                })
                .collect(Collectors.joining(", "));
        return new PreparedStatementSpec(sql, allParameters);
    }
}
