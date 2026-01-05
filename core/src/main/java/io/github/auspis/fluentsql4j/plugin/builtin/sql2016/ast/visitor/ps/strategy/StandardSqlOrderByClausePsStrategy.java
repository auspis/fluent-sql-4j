package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dql.clause.OrderBy;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.OrderByClausePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlOrderByClausePsStrategy implements OrderByClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(OrderBy clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var sorting : clause.sortings()) {
            PreparedStatementSpec res = sorting.accept(renderer, ctx);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PreparedStatementSpec(sql, params);
    }
}
