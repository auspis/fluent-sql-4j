package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.predicate.logical.Not;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.NotPsStrategy;

public class StandardSqlNotPsStrategy implements NotPsStrategy {
    @Override
    public PreparedStatementSpec handle(Not expression, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec inner = expression.expression().accept(renderer, ctx);
        String sql = "NOT (" + inner.sql() + ")";
        return new PreparedStatementSpec(sql, inner.parameters());
    }
}
