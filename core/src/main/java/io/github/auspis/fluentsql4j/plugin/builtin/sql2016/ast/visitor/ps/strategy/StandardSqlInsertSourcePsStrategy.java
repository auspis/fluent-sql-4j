package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertSource;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext.Feature;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class StandardSqlInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PreparedStatementSpec handle(InsertSource item, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec spec = item.setExpression().accept(renderer, new AstContext(Feature.UNION));
        return new PreparedStatementSpec(spec.sql(), spec.parameters());
    }
}
