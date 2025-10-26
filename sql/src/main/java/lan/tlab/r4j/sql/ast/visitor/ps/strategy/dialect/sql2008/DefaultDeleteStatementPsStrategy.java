package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.statement.dml.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DeleteStatementPsStrategy;

public class DefaultDeleteStatementPsStrategy implements DeleteStatementPsStrategy {
    @Override
    public PsDto handle(DeleteStatement stmt, Visitor<PsDto> renderer, AstContext ctx) {
        TableExpression table = stmt.getTable();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        String tableName =
                table instanceof lan.tlab.r4j.sql.ast.identifier.TableIdentifier t ? t.name() : table.toString();
        sql.append("DELETE FROM ").append(tableName);
        Where where = stmt.getWhere();
        if (where != null
                && where.getCondition() != null
                && !(where.getCondition() instanceof lan.tlab.r4j.sql.ast.predicate.NullPredicate)) {
            PsDto whereDto = where.accept(renderer, ctx);
            sql.append(" WHERE ").append(whereDto.sql());
            params.addAll(whereDto.parameters());
        }
        return new PsDto(sql.toString(), params);
    }
}
