package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NullPredicatePsStrategy;

public class StandardSqlNullPredicatePsStrategy implements NullPredicatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            NullPredicate expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        return new PreparedStatementSpec("NULL", List.of());
    }
}
