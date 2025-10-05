package lan.tlab.r4j.sql.ast.statement.ddl.definition;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

public interface ConstraintDefinition extends Visitable {

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class PrimaryKeyDefinition implements ConstraintDefinition {
        @Singular
        private final List<String> columns;

        public PrimaryKeyDefinition(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class NotNullConstraintDefinition implements ConstraintDefinition {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public class UniqueConstraintDefinition implements ConstraintDefinition {

        private final List<String> columns;

        public UniqueConstraintDefinition(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class ForeignKeyConstraintDefinition implements ConstraintDefinition {
        private final List<String> columns;
        private final ReferencesItem references;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class CheckConstraintDefinition implements ConstraintDefinition {
        private final Predicate expression;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class DefaultConstraintDefinition implements ConstraintDefinition {
        private final ScalarExpression value;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
