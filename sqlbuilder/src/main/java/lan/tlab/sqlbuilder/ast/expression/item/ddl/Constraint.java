package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.bool.BooleanExpression;
import lan.tlab.sqlbuilder.ast.expression.item.SqlItem;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

public interface Constraint extends SqlItem {

    @AllArgsConstructor
    @Getter
    public static class PrimaryKey implements Constraint {
        @Singular
        private final List<String> columns;

        public PrimaryKey(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @NoArgsConstructor
    public static class NotNullConstraint implements Constraint {
        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    public class UniqueConstraint implements Constraint {

        private final List<String> columns;

        public UniqueConstraint(String... columns) {
            this(Stream.of(columns).toList());
        }

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class ForeignKeyConstraint implements Constraint {
        private final List<String> columns;
        private final ReferencesItem references;

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class CheckConstraint implements Constraint {
        private final BooleanExpression expression;

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DefaultConstraint implements Constraint {
        private final ScalarExpression value;

        @Override
        public <T> T accept(SqlVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }
}
