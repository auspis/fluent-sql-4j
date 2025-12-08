package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeUsingPsStrategy;

public class StandardSqlMergeUsingPsStrategy implements MergeUsingPsStrategy {

    @Override
    public PreparedStatementSpec handle(MergeUsing item, PreparedStatementRenderer visitor, AstContext ctx) {
        return item.source().accept(visitor, ctx);
    }
}
