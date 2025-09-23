package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.statement.DeleteStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.DeleteStatementPsStrategy;

public class DefaultDeleteStatementPsStrategy implements DeleteStatementPsStrategy {
    @Override
    public PsDto handle(DeleteStatement stmt, Visitor<PsDto> visitor, AstContext ctx) {
        TableExpression table = stmt.getTable();
        StringBuilder sql = new StringBuilder();
        List<Object> params = new ArrayList<>();
        String tableName =
                table instanceof lan.tlab.sqlbuilder.ast.expression.item.Table t ? t.getName() : table.toString();
        sql.append("DELETE FROM ").append(tableName);
        Where where = stmt.getWhere();
        if (where != null
                && where.getCondition() != null
                && !(where.getCondition() instanceof lan.tlab.sqlbuilder.ast.expression.bool.NullBooleanExpression)) {
            PsDto whereDto = where.accept(visitor, ctx);
            sql.append(" WHERE ").append(whereDto.sql());
            params.addAll(whereDto.parameters());
        }
        return new PsDto(sql.toString(), params);
    }
}
