package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.GroupByClausePsStrategy;

public class DefaultGroupByClausePsStrategy implements GroupByClausePsStrategy {
    @Override
    public PsDto handle(GroupBy clause, Visitor<PsDto> renderer, AstContext ctx) {
        List<String> exprSqls = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : clause.groupingExpressions()) {
            PsDto res = expr.accept(renderer, ctx);
            exprSqls.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", exprSqls);
        return new PsDto(sql, params);
    }
}
