package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.InsertSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext.Feature;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.InsertSourcePsStrategy;

public class StandardSqlInsertSourcePsStrategy implements InsertSourcePsStrategy {
    @Override
    public PreparedStatementSpec handle(InsertSource item, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec spec = item.setExpression().accept(renderer, new AstContext(Feature.UNION));
        return new PreparedStatementSpec(spec.sql(), spec.parameters());
    }
}
