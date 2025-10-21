package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;

public class DefaultCurrentDateTimePsStrategy implements CurrentDateTimePsStrategy {

    @Override
    public PsDto handle(CurrentDateTime currentDateTime, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PsDto("CURRENT_TIMESTAMP", List.of());
    }
}
