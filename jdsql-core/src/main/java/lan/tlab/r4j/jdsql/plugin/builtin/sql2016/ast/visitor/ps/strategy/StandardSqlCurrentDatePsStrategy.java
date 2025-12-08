package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class StandardSqlCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PreparedStatementSpec handle(CurrentDate currentDate, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PreparedStatementSpec("CURRENT_DATE", List.of());
    }
}
