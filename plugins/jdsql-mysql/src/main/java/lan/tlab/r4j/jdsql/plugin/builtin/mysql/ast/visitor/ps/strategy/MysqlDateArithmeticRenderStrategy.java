package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DateArithmeticPsStrategy;

public class MysqlDateArithmeticRenderStrategy implements DateArithmeticPsStrategy {

    @Override
    public PsDto handle(DateArithmetic dateArithmetic, PreparedStatementRenderer renderer, AstContext ctx) {
        PsDto dateExprDto = dateArithmetic.dateExpression().accept(renderer, ctx);
        PsDto valueDto = dateArithmetic.interval().value().accept(renderer, ctx);

        String sql = String.format(
                "%s(%s, INTERVAL %s %s)",
                dateArithmetic.isAddition() ? "DATE_ADD" : "DATE_SUB",
                dateExprDto.sql(),
                valueDto.sql(),
                dateArithmetic.interval().unit().name());

        List<Object> allParameters = new ArrayList<>();
        allParameters.addAll(dateExprDto.parameters());
        allParameters.addAll(valueDto.parameters());

        return new PsDto(sql, allParameters);
    }
}
