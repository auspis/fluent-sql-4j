package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class StandardSqlCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PsDto handle(CurrentDate currentDate, PreparedStatementRenderer renderer, AstContext ctx) {
        // CURRENT_DATE is a standard SQL function that doesn't require parameters
        return new PsDto("CURRENT_DATE", List.of());
    }
}
