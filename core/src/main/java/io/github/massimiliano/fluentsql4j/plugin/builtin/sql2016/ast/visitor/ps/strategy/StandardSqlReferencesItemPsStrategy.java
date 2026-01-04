package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.ReferencesItemPsStrategy;
import java.util.Collections;
import java.util.stream.Collectors;

public class StandardSqlReferencesItemPsStrategy implements ReferencesItemPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            ReferencesItem item, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        EscapeStrategy escapeStrategy = astToPsSpecVisitor.getEscapeStrategy();

        String sql = String.format(
                "REFERENCES %s (%s)",
                escapeStrategy.apply(item.table()),
                item.columns().stream().map(escapeStrategy::apply).collect(Collectors.joining(", ")));

        return new PreparedStatementSpec(sql, Collections.emptyList());
    }
}
