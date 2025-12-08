package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.OrderByClausePsStrategy;

public class StandardSqlOrderByClausePsStrategy implements OrderByClausePsStrategy {
    @Override
    public PreparedStatementSpec handle(OrderBy clause, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (var sorting : clause.sortings()) {
            PreparedStatementSpec res = sorting.accept(renderer, ctx);
            sqlParts.add(res.sql());
            params.addAll(res.parameters());
        }
        String sql = String.join(", ", sqlParts);
        return new PreparedStatementSpec(sql, params);
    }
}
