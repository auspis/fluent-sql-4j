package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.FromSubqueryPsStrategy;

public class DefaultFromSubqueryPsStrategy implements FromSubqueryPsStrategy {

    @Override
    public PsDto handle(FromSubquery fromSubquery, Visitor<PsDto> visitor, AstContext ctx) {
        PsDto subqueryResult = fromSubquery.getSubquery().accept(visitor, ctx);

        String sql = "(" + subqueryResult.sql() + ")";

        // Add alias if present (not empty name)
        if (!fromSubquery.getAs().getName().isEmpty()) {
            PsDto aliasResult = fromSubquery.getAs().accept(visitor, ctx);
            sql += " AS " + aliasResult.sql();
        }

        return new PsDto(sql, subqueryResult.parameters());
    }
}
