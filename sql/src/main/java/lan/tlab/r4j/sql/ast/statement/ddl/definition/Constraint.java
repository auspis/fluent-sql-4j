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

public interface Constraint extends Visitable {

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class PrimaryKey implements Constraint {
        @Singular
        private final List<String> columns;

        public PrimaryKey(String... columns) {
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
    public static class NotNullConstraint implements Constraint {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    @ToString
    public class UniqueConstraint implements Constraint {

        private final List<String> columns;

        public UniqueConstraint(String... columns) {
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
    public static class ForeignKeyConstraint implements Constraint {
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
    public static class CheckConstraint implements Constraint {
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
    public static class DefaultConstraint implements Constraint {
        private final ScalarExpression value;

        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
