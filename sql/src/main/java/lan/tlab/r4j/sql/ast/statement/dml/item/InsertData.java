package lan.tlab.r4j.sql.ast.statement.dml.item;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.set.SetExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public interface InsertData extends Visitable {

    public static record DefaultValues() implements InsertData {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record InsertValues(List<Expression> valueExpressions) implements InsertData {

        public static InsertValues of(Expression... expressions) {
            return new InsertValues(Stream.of(expressions).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record InsertSource(SetExpression setExpression) implements InsertData {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
