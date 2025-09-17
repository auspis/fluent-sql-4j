package lan.tlab.sqlbuilder.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;

public class DefaultGroupByClausePsStrategy implements GroupByClausePsStrategy {
    @Override
    public PsDto handle(GroupBy clause, Visitor<PsDto> visitor, AstContext ctx) {
        List<String> exprSqls = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var expr : clause.getGroupingExpressions()) {
            PsDto res = expr.accept(visitor, ctx);
            exprSqls.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", exprSqls);
        return new PsDto(sql, params);
    }
}
