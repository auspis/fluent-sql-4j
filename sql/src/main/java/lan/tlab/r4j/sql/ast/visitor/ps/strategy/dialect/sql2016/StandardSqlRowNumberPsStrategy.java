package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.RowNumber;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.RowNumberPsStrategy;

public class StandardSqlRowNumberPsStrategy implements RowNumberPsStrategy {

    @Override
    public PsDto handle(RowNumber rowNumber, Visitor<PsDto> visitor, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ROW_NUMBER()");
        List<Object> parameters = new ArrayList<>();

        if (rowNumber.overClause() != null) {
            PsDto overResult = rowNumber.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
