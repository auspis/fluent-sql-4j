package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.DefaultValuesPsStrategy;

public class StandardSqlDefaultValuesPsStrategy implements DefaultValuesPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            DefaultValues defaultValues, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PreparedStatementSpec("DEFAULT VALUES", List.of());
    }
}
