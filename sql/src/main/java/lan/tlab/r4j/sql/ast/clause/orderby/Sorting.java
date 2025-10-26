package lan.tlab.r4j.sql.ast.clause.orderby;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Sorting(ScalarExpression expression, SortOrder sortOrder) implements Visitable {

    public enum SortOrder {
        ASC("ASC"),
        DESC("DESC"),
        DEFAULT("");

        private final String sqlKeyword;

        SortOrder(String sqlKeyword) {
            this.sqlKeyword = sqlKeyword;
        }

        public String getSqlKeyword() {
            return sqlKeyword;
        }
    }

    public static Sorting asc(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.ASC);
    }

    public static Sorting desc(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.DESC);
    }

    public static Sorting by(ScalarExpression expression) {
        return new Sorting(expression, SortOrder.DEFAULT);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
