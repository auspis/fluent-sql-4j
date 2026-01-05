package io.github.auspis.fluentsql4j.ast.dml.component;

import io.github.auspis.fluentsql4j.ast.core.expression.Expression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.SetExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import java.util.List;
import java.util.stream.Stream;

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
