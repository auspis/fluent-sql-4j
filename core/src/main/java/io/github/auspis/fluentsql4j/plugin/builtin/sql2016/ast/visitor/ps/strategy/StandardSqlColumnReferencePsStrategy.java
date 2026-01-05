package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ColumnReferencePsStrategy;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.EscapeStrategy;

public class StandardSqlColumnReferencePsStrategy implements ColumnReferencePsStrategy {
    @Override
    public PreparedStatementSpec handle(ColumnReference col, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        EscapeStrategy escapeStrategy = renderer.getEscapeStrategy();
        if (renderer instanceof AstToPreparedStatementSpecVisitor astToPsSpecVisitor) {
            escapeStrategy = astToPsSpecVisitor.getEscapeStrategy();
        }

        // Asterisk (*) is a special SQL wildcard and should not be escaped
        if ("*".equals(col.column())) {
            return new PreparedStatementSpec("*", List.of());
        }

        // Qualify column references in JOIN, UNION, and SUBQUERY contexts to avoid ambiguity
        boolean qualify = ctx.hasFeature(AstContext.Feature.JOIN_ON)
                || ctx.hasFeature(AstContext.Feature.UNION)
                || ctx.hasFeature(AstContext.Feature.SUBQUERY);
        String sql;
        if (qualify && !col.table().isBlank()) {
            sql = escapeStrategy.apply(col.table()) + "." + escapeStrategy.apply(col.column());
        } else {
            sql = escapeStrategy.apply(col.column());
        }
        return new PreparedStatementSpec(sql, List.of());
    }
}
