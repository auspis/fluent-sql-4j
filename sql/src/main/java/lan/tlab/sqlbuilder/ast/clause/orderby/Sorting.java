package lan.tlab.sqlbuilder.ast.clause.orderby;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Sorting implements Visitable {

    private final ScalarExpression expression;
    private final SortOrder sortOrder;

    @AllArgsConstructor
    public enum SortOrder {
        ASC("ASC"),
        DESC("DESC"),
        DEFAULT("");

        private final String sqlKeyword;

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
