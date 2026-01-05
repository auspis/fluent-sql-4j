package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeUsingPsStrategy;

public class StandardSqlMergeUsingPsStrategy implements MergeUsingPsStrategy {

    @Override
    public PreparedStatementSpec handle(MergeUsing item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        return item.source().accept(visitor, ctx);
    }
}
