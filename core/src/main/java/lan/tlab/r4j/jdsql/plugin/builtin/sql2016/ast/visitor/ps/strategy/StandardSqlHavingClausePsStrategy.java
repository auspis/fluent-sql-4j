package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.HavingClausePsStrategy;

public class StandardSqlHavingClausePsStrategy implements HavingClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Having clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        if (clause.condition() == null || clause.condition() instanceof NullPredicate) {
            return new PreparedStatementSpec("", java.util.List.of());
        }
        return clause.condition().accept(renderer, ctx);
    }
}
