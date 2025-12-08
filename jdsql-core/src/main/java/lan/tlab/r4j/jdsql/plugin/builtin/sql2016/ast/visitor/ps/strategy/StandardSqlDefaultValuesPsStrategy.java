package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData.DefaultValues;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DefaultValuesPsStrategy;

public class StandardSqlDefaultValuesPsStrategy implements DefaultValuesPsStrategy {
    @Override
    public PreparedStatementSpec handle(
            DefaultValues defaultValues, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // For SQL DEFAULT VALUES
        return new PreparedStatementSpec("DEFAULT VALUES", List.of());
    }
}
