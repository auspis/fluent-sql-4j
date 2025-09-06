package lan.tlab.sqlbuilder.ast.statement;

import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class DeleteStatement implements DataManipulationStatement {

    private final TableExpression table;

    @Default
    private final Where where = Where.builder().build();

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
