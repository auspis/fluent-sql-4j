package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.IsNull;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IsNullPsStrategy;

public class StandardSqlIsNullPsStrategy implements IsNullPsStrategy {
    @Override
    public PreparedStatementSpec handle(IsNull isNull, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = isNull.expression().accept(renderer, ctx);
        return new PreparedStatementSpec(inner.sql() + " IS NULL", inner.parameters());
    }
}
