package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.DateArithmeticPsStrategy;
import java.util.ArrayList;
import java.util.List;

public class MysqlDateArithmeticRenderStrategy implements DateArithmeticPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            DateArithmetic dateArithmetic, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec dateExprDto = dateArithmetic.dateExpression().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec valueDto = dateArithmetic.interval().value().accept(astToPsSpecVisitor, ctx);

        String sql = String.format(
                "%s(%s, INTERVAL %s %s)",
                dateArithmetic.isAddition() ? "DATE_ADD" : "DATE_SUB",
                dateExprDto.sql(),
                valueDto.sql(),
                dateArithmetic.interval().unit().name());

        List<Object> allParameters = new ArrayList<>();
        allParameters.addAll(dateExprDto.parameters());
        allParameters.addAll(valueDto.parameters());

        return new PreparedStatementSpec(sql, allParameters);
    }
}
