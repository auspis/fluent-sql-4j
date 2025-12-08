package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.AsPsStrategy;

public class StandardSqlAsPsStrategy implements AsPsStrategy {
    @Override
    public PreparedStatementSpec handle(Alias as, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        String sql = "\"" + as.name() + "\"";
        return new PreparedStatementSpec(sql, List.of());
    }
}
