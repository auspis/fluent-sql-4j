package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.Lag;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LagPsStrategy;

public class StandardSqlLagPsStrategy implements LagPsStrategy {

    @Override
    public PsDto handle(Lag lag, Visitor<PsDto> visitor, AstContext ctx) {
        List<Object> parameters = new ArrayList<>();
        PsDto exprResult = lag.expression().accept(visitor, ctx);
        parameters.addAll(exprResult.parameters());

        StringBuilder sql =
                new StringBuilder("LAG(").append(exprResult.sql()).append(", ").append(lag.offset());

        if (lag.defaultValue() != null) {
            PsDto defaultResult = lag.defaultValue().accept(visitor, ctx);
            sql.append(", ").append(defaultResult.sql());
            parameters.addAll(defaultResult.parameters());
        }

        sql.append(")");

        if (lag.overClause() != null) {
            PsDto overResult = lag.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
