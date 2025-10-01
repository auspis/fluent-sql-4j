package lan.tlab.sqlbuilder.dsl.table;

import java.util.ArrayList;
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
        private final String columnName;
        private DataType dataType;
        private NotNullConstraint notNullConstraint;
        private boolean isPrimaryKey = false;

        public ColumnBuilder(TableBuilder tableBuilder, String columnName) {
            this.tableBuilder = tableBuilder;
            this.columnName = columnName;
        }

        public ColumnBuilder integer() {
            return dataType(DataType.integer());
        }

        public ColumnBuilder varchar(int length) {
            return dataType(DataType.varchar(length));
        }

        public ColumnBuilder date() {
            return dataType(DataType.date());
        }

        public ColumnBuilder timestamp() {
            return dataType(DataType.timestamp());
        }

        public ColumnBuilder bool() {
            return dataType(DataType.bool());
        }

        public ColumnBuilder decimal(int precision, int scale) {
            return dataType(DataType.decimal(precision, scale));
        }

        private ColumnBuilder dataType(DataType value) {
            dataType = value;
            return this;
        }

        public ColumnBuilder primaryKey() {
            isPrimaryKey = true;
            return this;
        }

        public ColumnBuilder notNull() {
            notNullConstraint = new NotNullConstraint();
            return this;
        }

        public ColumnBuilder column(String columnName) {
            buildColumn();
            return tableBuilder.column(columnName);
        }

        public String build() {
            buildColumn();
            return tableBuilder.build();
        }

        void buildColumn() {
            if (dataType == null) {
                throw new IllegalStateException("Data type must be specified for column: " + columnName);
            }

            ColumnDefinition.ColumnDefinitionBuilder builder =
                    ColumnDefinition.builder().name(columnName).type(dataType);

            if (notNullConstraint != null) {
                builder.notNullConstraint(notNullConstraint);
            }

            ColumnDefinition columnDefinition = builder.build();
            tableBuilder.addColumn(columnDefinition);

            if (isPrimaryKey) {
                tableBuilder.addPrimaryKeyColumn(columnName);
            }
        }
    }

    private final String tableName;
    private final List<ColumnDefinition> columns = new ArrayList<>();
    private final List<String> primaryKeyColumns = new ArrayList<>();
    private final SqlRenderer sqlRenderer;

    public TableBuilder(String tableName) {
        this(SqlRendererFactory.standardSql2008(), tableName);
    }

    public TableBuilder(SqlRenderer sqlRenderer, String tableName) {
        this.sqlRenderer = sqlRenderer;
        this.tableName = tableName;
    }

    public TableBuilder.ColumnBuilder column(String columnName) {
        return new TableBuilder.ColumnBuilder(this, columnName);
    }

    public TableBuilder columnIntegerPrimaryKey(String columnName) {
        column(columnName).integer().notNull().primaryKey().buildColumn();
        return this;
    }

    public TableBuilder columnStringPrimaryKey(String columnName, int length) {
        column(columnName).varchar(length).notNull().primaryKey().buildColumn();
        return this;
    }

    public TableBuilder columnTimestampNotNull(String columnName) {
        column(columnName).timestamp().notNull().buildColumn();
        return this;
    }

    public TableBuilder columnVarcharNotNull(String columnName, int length) {
        column(columnName).varchar(length).notNull().buildColumn();
        return this;
    }

    public TableBuilder columnDecimalNotNull(String columnName, int precision, int scale) {
        column(columnName).decimal(precision, scale).notNull().buildColumn();
        return this;
    }

    void addColumn(ColumnDefinition column) {
        columns.add(column);
    }

    void addPrimaryKeyColumn(String columnName) {
        primaryKeyColumns.add(columnName);
    }

    public String build() {
        CreateTableStatement statement = buildAst();
        return statement.accept(sqlRenderer, new AstContext());
    }

    private CreateTableStatement buildAst() {
        TableDefinition.TableDefinitionBuilder builder =
                TableDefinition.builder().name(tableName).columns(columns);

        if (!primaryKeyColumns.isEmpty()) {
            builder.primaryKey(new PrimaryKey(primaryKeyColumns));
        }

        TableDefinition tableDefinition = builder.build();
        return new CreateTableStatement(tableDefinition);
    }
}
