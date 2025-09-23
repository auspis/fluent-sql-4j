package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;

public class DefaultCurrentDateTimePsStrategy implements CurrentDateTimePsStrategy {

    @Override
    public PsDto handle(CurrentDateTime currentDateTime, PreparedStatementVisitor visitor, AstContext ctx) {
        return new PsDto("CURRENT_TIMESTAMP", List.of());
    }
}
