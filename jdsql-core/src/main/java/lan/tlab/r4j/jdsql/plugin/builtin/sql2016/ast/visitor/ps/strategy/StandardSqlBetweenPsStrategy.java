package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.BetweenPsStrategy;

public class StandardSqlBetweenPsStrategy implements BetweenPsStrategy {
    @Override
    public PreparedStatementSpec handle(Between between, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        PreparedStatementSpec testDto = between.testExpression().accept(renderer, ctx);
        PreparedStatementSpec startDto = between.startExpression().accept(renderer, ctx);
        PreparedStatementSpec endDto = between.endExpression().accept(renderer, ctx);

        String sql = testDto.sql() + " BETWEEN " + startDto.sql() + " AND " + endDto.sql();

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(testDto.parameters());
        parameters.addAll(startDto.parameters());
        parameters.addAll(endDto.parameters());

        return new PreparedStatementSpec(sql, parameters);
    }
}
