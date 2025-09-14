package lan.tlab.sqlbuilder.ast.expression.item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.expression.set.SetExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface InsertData extends SqlItem {

    @AllArgsConstructor
    @Getter
    public static class DefaultValues implements InsertData {
        @Override
        public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class InsertValues implements InsertData {
        private List<Expression> valueExpressions = new ArrayList<>();

        public static InsertValues of(Expression... expressions) {
            return new InsertValues(Stream.of(expressions).toList());
        }

        @Override
        public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class InsertSource implements InsertData {
        private SetExpression setExpression = new NullSetExpression();

        @Override
        public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
