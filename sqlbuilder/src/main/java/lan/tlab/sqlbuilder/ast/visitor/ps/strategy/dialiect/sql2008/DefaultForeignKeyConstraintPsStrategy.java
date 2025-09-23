package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.ForeignKeyConstraintPsStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;

public class DefaultForeignKeyConstraintPsStrategy implements ForeignKeyConstraintPsStrategy {

    private final SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();

    @Override
    public PsDto handle(ForeignKeyConstraint constraint, PreparedStatementVisitor visitor, AstContext ctx) {
        // Foreign key constraints are static DDL elements without parameters
        String sql = constraint.accept(sqlRenderer, ctx);
        return new PsDto(sql, List.of());
    }
}
