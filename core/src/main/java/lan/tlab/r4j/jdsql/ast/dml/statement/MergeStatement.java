package lan.tlab.r4j.jdsql.ast.dml.statement;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class MergeStatement implements DataManipulationStatement {

    private final TableIdentifier targetTable;

    private final MergeUsing using;

    private final Predicate onCondition;

    @Default
    private final List<MergeAction> actions = new ArrayList<>();

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
