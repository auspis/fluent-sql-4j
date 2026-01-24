package io.github.auspis.fluentsql4j.ast.dml.component;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.dsl.util.ColumnReferenceUtil;

public record UpdateItem(ColumnReference column, ScalarExpression value) implements Visitable {

    public static UpdateItem of(String column, ScalarExpression value) {
        return new UpdateItem(ColumnReferenceUtil.createValidated(column), value);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
