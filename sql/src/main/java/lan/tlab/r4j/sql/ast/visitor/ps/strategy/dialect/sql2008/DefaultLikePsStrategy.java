package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.predicate.Like;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.LikePsStrategy;

public class DefaultLikePsStrategy implements LikePsStrategy {

    @Override
    public PsDto handle(Like like, PreparedStatementVisitor visitor, AstContext ctx) {
        PsDto expressionDto = like.getExpression().accept(visitor, ctx);

        StringBuilder sql = new StringBuilder();
        sql.append(expressionDto.sql());
        sql.append(" LIKE ?");

        List<Object> parameters = new ArrayList<>(expressionDto.parameters());
        parameters.add(like.getPattern());

        return new PsDto(sql.toString(), parameters);
    }
}
