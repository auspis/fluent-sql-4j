package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lag;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LagPsStrategy;

public class DefaultLagPsStrategy implements LagPsStrategy {

    @Override
    public PsDto handle(Lag lag, Visitor<PsDto> visitor, AstContext ctx) {
        List<Object> parameters = new ArrayList<>();
        PsDto exprResult = lag.getExpression().accept(visitor, ctx);
        parameters.addAll(exprResult.parameters());

        StringBuilder sql =
                new StringBuilder("LAG(").append(exprResult.sql()).append(", ").append(lag.getOffset());

        if (lag.getDefaultValue() != null) {
            PsDto defaultResult = lag.getDefaultValue().accept(visitor, ctx);
            sql.append(", ").append(defaultResult.sql());
            parameters.addAll(defaultResult.parameters());
        }

        sql.append(")");

        if (lag.getOverClause() != null) {
            PsDto overResult = lag.getOverClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
