package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CurrentDatePsStrategy;

public class MysqlCurrentDatePsStrategy implements CurrentDatePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CurrentDate currentDate, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        return new PreparedStatementSpec("CURDATE()", List.of());
    }
}
