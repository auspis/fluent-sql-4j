package io.github.auspis.fluentsql4j.ast.ddl.definition;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import java.util.List;
import java.util.stream.Stream;

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
