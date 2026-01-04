package io.github.massimiliano.fluentsql4j.ast.ddl.definition;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record ColumnDefinition(
        String name,
        DataType type,
        NotNullConstraintDefinition notNullConstraint,
        DefaultConstraintDefinition defaultConstraint)
        implements Visitable {

    public ColumnDefinition(
            String name,
            DataType type,
            NotNullConstraintDefinition notNullConstraint,
            DefaultConstraintDefinition defaultConstraint) {
        this.name = name;
        this.type = type != null ? type : DataType.varchar(255);
        this.notNullConstraint = notNullConstraint;
        this.defaultConstraint = defaultConstraint;
    }

    public static ColumnDefinition nullObject() {
        return new ColumnDefinition(null, null, null, null);
    }

    public static ColumnDefinitionBuilder builder() {
        return new ColumnDefinitionBuilder();
    }

    public static ColumnDefinitionBuilder builder(String name, DataType type) {
        return new ColumnDefinitionBuilder().name(name).type(type);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static class ColumnDefinitionBuilder {
        private String name;
        private DataType type = DataType.varchar(255);
        private NotNullConstraintDefinition notNullConstraint;
        private DefaultConstraintDefinition defaultConstraint;

        public ColumnDefinitionBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ColumnDefinitionBuilder type(DataType type) {
            this.type = type;
            return this;
        }

        public ColumnDefinitionBuilder notNullConstraint(NotNullConstraintDefinition notNullConstraint) {
            this.notNullConstraint = notNullConstraint;
            return this;
        }

        public ColumnDefinitionBuilder defaultConstraint(DefaultConstraintDefinition defaultConstraint) {
            this.defaultConstraint = defaultConstraint;
            return this;
        }

        public ColumnDefinition build() {
            return new ColumnDefinition(name, type, notNullConstraint, defaultConstraint);
        }

        public static ColumnDefinitionBuilder integer(String name) {
            return builder(name, DataType.integer());
        }

        public static ColumnDefinitionBuilder varchar(String name) {
            return builder(name, DataType.varchar(255));
        }

        public static ColumnDefinitionBuilder date(String name) {
            return builder(name, DataType.date());
        }

        public static ColumnDefinitionBuilder bool(String name) {
            return builder(name, DataType.bool());
        }
    }
}
