package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.GroupByClausePsStrategy;

public class StandardSqlGroupByClausePsStrategy implements GroupByClausePsStrategy {
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
