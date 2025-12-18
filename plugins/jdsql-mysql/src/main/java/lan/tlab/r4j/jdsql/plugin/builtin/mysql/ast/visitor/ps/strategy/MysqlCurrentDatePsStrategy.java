package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class MysqlCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CurrentDate currentDate, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        return new PreparedStatementSpec("CURDATE()", List.of());
    }
}
