package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TablePsStrategy;

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
