package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.predicate.logical.LogicalOperator;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.AndOrPsStrategy;

public class DefaultAndOrPsStrategy implements AndOrPsStrategy {
    @Override
    public PsDto handle(AndOr expression, Visitor<PsDto> renderer, AstContext ctx) {
        String operator = expression.getOperator() == LogicalOperator.AND ? "AND" : "OR";
        List<String> sqlParts = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        for (Predicate expr : expression.getOperands()) {
            PsDto res = expr.accept(renderer, ctx);
            sqlParts.add("(" + res.sql() + ")");
            params.addAll(res.parameters());
        }
        String sql = String.join(" " + operator + " ", sqlParts);
        return new PsDto(sql, params);
    }
}
