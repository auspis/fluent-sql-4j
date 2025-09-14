package lan.tlab.sqlbuilder.ast.statement;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class InsertStatement implements DataManipulationStatement {

    private final TableExpression table;

    @Default
    private final List<ColumnReference> columns = new ArrayList<>();

    @Default
    private final InsertData data = new DefaultValues();

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this);
    }
}
