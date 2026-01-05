package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.AsPsStrategy;
import java.util.List;

public class StandardSqlAsPsStrategy implements AsPsStrategy {
    @Override
    public PreparedStatementSpec handle(Alias as, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        String sql = "\"" + as.name() + "\"";
        return new PreparedStatementSpec(sql, List.of());
    }
}
