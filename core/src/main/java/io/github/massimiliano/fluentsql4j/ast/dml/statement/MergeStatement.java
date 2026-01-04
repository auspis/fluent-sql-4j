package io.github.massimiliano.fluentsql4j.ast.dml.statement;

import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeAction;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;
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
