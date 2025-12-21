package lan.tlab.r4j.jdsql.dsl.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType;

public class ColumnBuilder {
    private final CreateTableBuilder tableBuilder;
    private final ColumnDefinition.ColumnDefinitionBuilder columnBuilder;

    public ColumnBuilder(CreateTableBuilder tableBuilder, String columnName) {
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

    public CreateTableBuilder primaryKey(String... columnNames) {
        buildAndAdd();
        return tableBuilder.primaryKey(columnNames);
    }

    public CreateTableBuilder index(String indexName, String... columns) {
        buildAndAdd();
        return tableBuilder.index(indexName, columns);
    }

    public CreateTableBuilder unique() {
        ColumnDefinition columnDef = buildAndAdd();
        return tableBuilder.unique(columnDef.name());
    }

    public CreateTableBuilder foreignKey(String refTable, String... refColumns) {
        ColumnDefinition columnDef = buildAndAdd();
        return tableBuilder.foreignKey(columnDef.name(), refTable, refColumns);
    }

    public CreateTableBuilder check(Predicate expr) {
        buildAndAdd();
        return tableBuilder.check(expr);
    }

    public CreateTableBuilder defaultValue(ScalarExpression value) {
        buildAndAdd();
        return tableBuilder.defaultConstraint(value);
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        buildAndAdd();
        return tableBuilder.build(connection);
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
