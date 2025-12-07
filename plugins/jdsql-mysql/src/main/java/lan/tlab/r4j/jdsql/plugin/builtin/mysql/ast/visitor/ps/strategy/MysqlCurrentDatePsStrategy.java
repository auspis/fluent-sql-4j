package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class MysqlCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PsDto handle(CurrentDate currentDate, PreparedStatementRenderer renderer, AstContext ctx) {
        return new PsDto("CURDATE()", List.of());
    }
}
