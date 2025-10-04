package lan.tlab.r4j.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.InsertData;
import lan.tlab.r4j.sql.ast.expression.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
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
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
