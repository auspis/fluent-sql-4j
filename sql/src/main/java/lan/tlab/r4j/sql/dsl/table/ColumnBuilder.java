package lan.tlab.r4j.sql.dsl.table;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType;

public class ColumnBuilder {
    private final TableBuilder tableBuilder;
    private final ColumnDefinition.ColumnDefinitionBuilder columnBuilder;

    public ColumnBuilder(TableBuilder tableBuilder, String columnName) {
        this.tableBuilder = tableBuilder;
        this.columnBuilder = ColumnDefinition.builder().name(columnName);
    }

    public ColumnBuilder integer() {
        columnBuilder.type(DataType.integer());
        return this;
    }

    public ColumnBuilder varchar(int length) {
        columnBuilder.type(DataType.varchar(length));
        return this;
    }

    public ColumnBuilder date() {
        columnBuilder.type(DataType.date());
        return this;
    }

    public ColumnBuilder timestamp() {
        columnBuilder.type(DataType.timestamp());
        return this;
    }

    public ColumnBuilder bool() {
        columnBuilder.type(DataType.bool());
        return this;
    }

    public ColumnBuilder decimal(int precision, int scale) {
        columnBuilder.type(DataType.decimal(precision, scale));
        return this;
    }

    public ColumnBuilder notNull() {
        columnBuilder.notNullConstraint(new NotNullConstraintDefinition());
        return this;
    }

    public ColumnBuilder column(String nextColumnName) {
        buildAndAdd();
        return new ColumnBuilder(tableBuilder, nextColumnName);
    }

    public TableBuilder primaryKey(String... columnNames) {
        buildAndAdd();
        return tableBuilder.primaryKey(columnNames);
    }

    public TableBuilder index(String indexName, String... columns) {
        buildAndAdd();
        return tableBuilder.index(indexName, columns);
    }

    public TableBuilder unique() {
        ColumnDefinition columnDef = buildAndAdd();
        return tableBuilder.unique(columnDef.getName());
    }

    public TableBuilder foreignKey(String refTable, String... refColumns) {
        ColumnDefinition columnDef = buildAndAdd();
        return tableBuilder.foreignKey(columnDef.getName(), refTable, refColumns);
    }

    public TableBuilder check(Predicate expr) {
        buildAndAdd();
        return tableBuilder.check(expr);
    }

    public TableBuilder defaultValue(ScalarExpression value) {
        buildAndAdd();
        return tableBuilder.defaultConstraint(value);
    }

    public String build() {
        buildAndAdd();
        return tableBuilder.build();
    }

    void buildColumn() {
        buildAndAdd();
    }

    private ColumnDefinition buildAndAdd() {
        ColumnDefinition columnDef = columnBuilder.build();
        tableBuilder.addColumn(columnDef);
        return columnDef;
    }
}
