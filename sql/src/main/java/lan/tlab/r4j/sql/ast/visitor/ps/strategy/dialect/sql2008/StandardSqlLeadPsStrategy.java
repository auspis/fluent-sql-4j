package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.window.Lead;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LeadPsStrategy;

public class StandardSqlLeadPsStrategy implements LeadPsStrategy {

    @Override
    public PsDto handle(Lead lead, Visitor<PsDto> visitor, AstContext ctx) {
        List<Object> parameters = new ArrayList<>();
        PsDto exprResult = lead.expression().accept(visitor, ctx);
        parameters.addAll(exprResult.parameters());

        StringBuilder sql =
                new StringBuilder("LEAD(").append(exprResult.sql()).append(", ").append(lead.offset());

        if (lead.defaultValue() != null) {
            PsDto defaultResult = lead.defaultValue().accept(visitor, ctx);
            sql.append(", ").append(defaultResult.sql());
            parameters.addAll(defaultResult.parameters());
        }

        sql.append(")");

        if (lead.overClause() != null) {
            PsDto overResult = lead.overClause().accept(visitor, ctx);
            sql.append(" ").append(overResult.sql());
            parameters.addAll(overResult.parameters());
        }

        return new PsDto(sql.toString(), parameters);
    }
}
