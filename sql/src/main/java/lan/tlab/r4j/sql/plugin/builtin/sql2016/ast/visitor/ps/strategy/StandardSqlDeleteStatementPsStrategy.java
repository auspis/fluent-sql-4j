package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DeleteStatementPsStrategy;

public class StandardSqlDeleteStatementPsStrategy implements DeleteStatementPsStrategy {
    @Override
    public PsDto handle(DeleteStatement stmt, Visitor<PsDto> renderer, AstContext ctx) {
        TableExpression table = stmt.table();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        String tableName =
                table instanceof lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier t ? t.name() : table.toString();
        sql.append("DELETE FROM ").append(tableName);
        Where where = stmt.where();
        if (where != null
                && where.condition() != null
                && !(where.condition() instanceof lan.tlab.r4j.sql.ast.common.predicate.NullPredicate)) {
            PsDto whereDto = where.accept(renderer, ctx);
            sql.append(" WHERE ").append(whereDto.sql());
            params.addAll(whereDto.parameters());
        }
        return new PsDto(sql.toString(), params);
    }
}
