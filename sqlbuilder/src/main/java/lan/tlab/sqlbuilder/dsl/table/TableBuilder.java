package lan.tlab.sqlbuilder.dsl.table;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.statement.CreateTableStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;

public class TableBuilder {

    public static class ColumnBuilder {
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

    private TableDefinition.TableDefinitionBuilder definitionBuilder;
    private final SqlRenderer sqlRenderer;

    public TableBuilder(String tableName) {
        this(SqlRendererFactory.standardSql2008(), tableName);
    }

    public TableBuilder(SqlRenderer sqlRenderer, String tableName) {
        this.sqlRenderer = sqlRenderer;
        this.definitionBuilder = TableDefinition.builder().name(tableName);
    }

    public ColumnBuilder column(String columnName) {
        return new ColumnBuilder(this, columnName);
    }

    public TableBuilder primaryKey(String... columnNames) {
        if (columnNames.length > 0) {
            definitionBuilder = definitionBuilder.primaryKey(new PrimaryKey(List.of(columnNames)));
        }
        return this;
    }

    public TableBuilder columnIntegerPrimaryKey(String columnName) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.integer())
                .notNullConstraint(new NotNullConstraint())
                .build();

        addColumn(columnDef);
        return primaryKey(columnName);
    }

    public TableBuilder columnStringPrimaryKey(String columnName, int length) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.varchar(length))
                .notNullConstraint(new NotNullConstraint())
                .build();

        addColumn(columnDef);
        return primaryKey(columnName);
    }

    public TableBuilder columnTimestampNotNull(String columnName) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.timestamp())
                .notNullConstraint(new NotNullConstraint())
                .build();

        addColumn(columnDef);
        return this;
    }

    public TableBuilder columnVarcharNotNull(String columnName, int length) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.varchar(length))
                .notNullConstraint(new NotNullConstraint())
                .build();

        addColumn(columnDef);
        return this;
    }

    public TableBuilder columnDecimalNotNull(String columnName, int precision, int scale) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.decimal(precision, scale))
                .notNullConstraint(new NotNullConstraint())
                .build();

        addColumn(columnDef);
        return this;
    }

    private void addColumn(ColumnDefinition column) {
        definitionBuilder = definitionBuilder.column(column);
    }

    public String build() {
        CreateTableStatement statement = new CreateTableStatement(definitionBuilder.build());
        return statement.accept(sqlRenderer, new AstContext());
    }
}
