package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;

public class StandardSqlCurrentDateTimePsStrategy implements CurrentDateTimePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CurrentDateTime currentDateTime, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PreparedStatementSpec("CURRENT_TIMESTAMP", List.of());
    }
}
