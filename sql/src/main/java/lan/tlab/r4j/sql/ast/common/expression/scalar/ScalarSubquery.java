package lan.tlab.r4j.sql.ast.common.expression.scalar;

import lan.tlab.r4j.sql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

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
