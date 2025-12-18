package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.predicate.Like;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.LikePsStrategy;

public class StandardSqlLikePsStrategy implements LikePsStrategy {

    @Override
    public PreparedStatementSpec handle(Like like, AstToPreparedStatementSpecVisitor renderer, AstContext ctx) {
        PreparedStatementSpec expressionDto = like.expression().accept(renderer, ctx);

        StringBuilder sql = new StringBuilder();
        sql.append(expressionDto.sql());
        sql.append(" LIKE ?");

        List<Object> parameters = new ArrayList<>(expressionDto.parameters());
        parameters.add(like.pattern());

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
