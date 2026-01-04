package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.core.predicate.IsNotNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNotNullPsStrategy;

public class StandardSqlIsNotNullPsStrategy implements IsNotNullPsStrategy {
    @Override
    public PreparedStatementSpec handle(IsNotNull isNotNull, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = isNotNull.expression().accept(renderer, ctx);
        return new PreparedStatementSpec(inner.sql() + " IS NOT NULL", inner.parameters());
    }
}
