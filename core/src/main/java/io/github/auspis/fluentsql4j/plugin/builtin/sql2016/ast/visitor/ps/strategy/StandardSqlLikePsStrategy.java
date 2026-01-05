package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.predicate.Like;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.LikePsStrategy;
import java.util.ArrayList;
import java.util.List;

public class StandardSqlLikePsStrategy implements LikePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Like like, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec expressionDto = like.expression().accept(astToPsSpecVisitor, ctx);

        StringBuilder sql = new StringBuilder();
        sql.append(expressionDto.sql());
        sql.append(" LIKE ?");

        List<Object> parameters = new ArrayList<>(expressionDto.parameters());
        parameters.add(like.pattern());

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
