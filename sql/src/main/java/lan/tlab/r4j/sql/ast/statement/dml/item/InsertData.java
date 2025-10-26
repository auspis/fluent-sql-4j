package lan.tlab.r4j.sql.ast.statement.dml.item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
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

    public static class InsertValues implements InsertData {
        private List<Expression> valueExpressions = new ArrayList<>();

        public InsertValues(List<Expression> valueExpressions) {
            this.valueExpressions = new ArrayList<>(valueExpressions);
        }

        public List<Expression> getValueExpressions() {
            return valueExpressions;
        }

        public static InsertValues of(Expression... expressions) {
            return new InsertValues(Stream.of(expressions).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static class InsertSource implements InsertData {
        private SetExpression setExpression = new NullSetExpression();

        public InsertSource(SetExpression setExpression) {
            this.setExpression = setExpression;
        }

        public SetExpression getSetExpression() {
            return setExpression;
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
