package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.From;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.FromClausePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlFromClausePsStrategy implements FromClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(From clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var source : clause.sources()) {
            PreparedStatementSpec res = source.accept(renderer, ctx);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PreparedStatementSpec(sql, params);
    }
}
