package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.WhereClausePsStrategy;

public class StandardSqlWhereClausePsStrategy implements WhereClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(Where where, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        Predicate cond = where.condition();
        if (cond instanceof NullPredicate) {
            return new PreparedStatementSpec("", List.of());
        }
        return cond.accept(renderer, ctx);
    }
}
