package lan.tlab.r4j.sql.ast.statement.dml;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class MergeStatement implements DataManipulationStatement {

    private final TableExpression targetTable;

    @Default
    private final Alias targetAlias = Alias.nullObject();

    private final MergeUsing using;

    private final Predicate onCondition;

    @Default
    private final List<MergeAction> actions = new ArrayList<>();

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
