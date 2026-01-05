package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.TablePsStrategy;

public class StandardSqlTablePsStrategy implements TablePsStrategy {
    @Override
    public PreparedStatementSpec handle(
            TableIdentifier table, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        AstToPreparedStatementSpecVisitor astToPsSpecVisitor = (AstToPreparedStatementSpecVisitor) renderer;
        String sql = astToPsSpecVisitor.getEscapeStrategy().apply(table.name());
        String alias = table.alias() != null ? table.alias().name() : null;
        if (alias != null && !alias.isBlank()) {
            sql += " AS " + alias;
        }
        return new PreparedStatementSpec(sql, List.of());
    }
}
