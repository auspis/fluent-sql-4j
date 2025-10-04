package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class DefaultCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PsDto handle(CurrentDate currentDate, PreparedStatementVisitor visitor, AstContext ctx) {
        // CURRENT_DATE is a standard SQL function that doesn't require parameters
        return new PsDto("CURRENT_DATE", List.of());
    }
}
