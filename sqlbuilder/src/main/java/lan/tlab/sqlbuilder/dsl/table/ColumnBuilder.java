package lan.tlab.sqlbuilder.dsl.table;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType;

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
        columnBuilder.notNullConstraint(new NotNullConstraint());
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

    public String build() {
        buildAndAdd();
        return tableBuilder.build();
    }

    void buildColumn() {
        buildAndAdd();
    }

    private void buildAndAdd() {
        ColumnDefinition columnDef = columnBuilder.build();
        tableBuilder.addColumn(columnDef);
    }
}
