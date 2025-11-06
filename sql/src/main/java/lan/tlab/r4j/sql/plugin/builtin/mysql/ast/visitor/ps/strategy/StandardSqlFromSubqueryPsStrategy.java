package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.FromSubqueryPsStrategy;

public class StandardSqlFromSubqueryPsStrategy implements FromSubqueryPsStrategy {

    @Override
    public PsDto handle(FromSubquery fromSubquery, Visitor<PsDto> renderer, AstContext ctx) {
        PsDto subqueryResult = fromSubquery.getSubquery().accept(renderer, ctx);

        String sql = "(" + subqueryResult.sql() + ")";

        // Add alias if present (not empty name)
        if (!fromSubquery.getAs().name().isEmpty()) {
            PsDto aliasResult = fromSubquery.getAs().accept(renderer, ctx);
            sql += " AS " + aliasResult.sql();
        }

        return new PsDto(sql, subqueryResult.parameters());
    }
}
