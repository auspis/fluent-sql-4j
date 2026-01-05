package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDateTime;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CurrentDateTimePsStrategy;
import java.util.List;

public class MysqlCurrentDateTimePsStrategy implements CurrentDateTimePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CurrentDateTime currentDateTime, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        return new PreparedStatementSpec("NOW()", List.of());
    }
}
