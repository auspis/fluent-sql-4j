package lan.tlab.r4j.sql.ast.ddl.definition;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public interface ConstraintDefinition extends Visitable {

    public static record PrimaryKeyDefinition(List<String> columns) implements ConstraintDefinition {

        public PrimaryKeyDefinition(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record NotNullConstraintDefinition() implements ConstraintDefinition {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record UniqueConstraintDefinition(List<String> columns) implements ConstraintDefinition {

        public UniqueConstraintDefinition(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record ForeignKeyConstraintDefinition(List<String> columns, ReferencesItem references)
            implements ConstraintDefinition {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record CheckConstraintDefinition(Predicate expression) implements ConstraintDefinition {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    public static record DefaultConstraintDefinition(ScalarExpression value) implements ConstraintDefinition {

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
