package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DeleteStatementPsStrategy;

public class StandardSqlDeleteStatementPsStrategy implements DeleteStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(DeleteStatement stmt, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        TableExpression table = stmt.table();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();

        // Use renderer to properly escape table name
        PreparedStatementSpec tableDto = table.accept(renderer, ctx);
        sql.append("DELETE FROM ").append(tableDto.sql());
        params.addAll(tableDto.parameters());

        Where where = stmt.where();
        if (where != null
                && where.condition() != null
                && !(where.condition() instanceof lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate)) {
            PreparedStatementSpec whereDto = where.accept(renderer, ctx);
            sql.append(" WHERE ").append(whereDto.sql());
            params.addAll(whereDto.parameters());
        }
        return new PreparedStatementSpec(sql.toString(), params);
    }
}
