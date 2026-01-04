package io.github.massimiliano.fluentsql4j.ast.core.expression.scalar;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record ScalarSubquery(TableExpression tableExpression) implements ScalarExpression {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TableExpression tableExpression;

        public Builder tableExpression(TableExpression tableExpression) {
            this.tableExpression = tableExpression;
            return this;
        }

        public ScalarSubquery build() {
            return new ScalarSubquery(tableExpression);
        }
    }
}
