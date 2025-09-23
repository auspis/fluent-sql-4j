package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.bool.Like;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.LikePsStrategy;

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
