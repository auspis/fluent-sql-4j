package lan.tlab.r4j.sql.dsl.table;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.bool.BooleanExpression;
import lan.tlab.r4j.sql.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.NotNullConstraint;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.expression.item.ddl.DataType;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Index;
import lan.tlab.r4j.sql.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.r4j.sql.ast.expression.item.ddl.TableDefinition;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.statement.CreateTableStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class TableBuilder {

    private TableDefinition.TableDefinitionBuilder definitionBuilder;
    private final SqlRenderer sqlRenderer;
    private List<ColumnDefinition> columns = new ArrayList<>();

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

    void addColumn(ColumnDefinition column) {
        // replace existing column with same name if present
        columns.removeIf(c -> c.getName().equals(column.getName()));
        columns.add(column);
    }

    public TableBuilder index(String indexName, String... columns) {
        if (indexName != null && columns != null && columns.length > 0) {
            definitionBuilder = definitionBuilder.index(new Index(indexName, columns));
        }
        return this;
    }

    public TableBuilder unique(String... columns) {
        if (columns != null && columns.length > 0) {
            definitionBuilder = definitionBuilder.constraint(new Constraint.UniqueConstraint(columns));
        }
        return this;
    }

    /**
     * Convenience method to add a single-column foreign key constraint.
     */
    public TableBuilder foreignKey(String column, String refTable, String... refColumns) {
        if (column != null && refTable != null) {
            definitionBuilder = definitionBuilder.constraint(
                    new Constraint.ForeignKeyConstraint(List.of(column), new ReferencesItem(refTable, refColumns)));
        }
        return this;
    }

    public TableBuilder check(BooleanExpression expr) {
        if (expr != null) {
            definitionBuilder = definitionBuilder.constraint(new Constraint.CheckConstraint(expr));
        }
        return this;
    }

    public TableBuilder defaultConstraint(ScalarExpression value) {
        if (value != null) {
            definitionBuilder = definitionBuilder.constraint(new Constraint.DefaultConstraint(value));
        }
        return this;
    }

    public TableBuilder notNullColumn(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            ColumnDefinition c = columns.get(i);
            if (c.getName().equals(columnName)) {
                ColumnDefinition updated = ColumnDefinition.builder()
                        .name(c.getName())
                        .type(c.getType())
                        .notNullConstraint(new NotNullConstraint())
                        .defaultConstraint(c.getDefaultConstraint())
                        .build();

                columns.set(i, updated);
                // columns list updated; will be applied once during build()
                return this;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

    public String build() {
        TableDefinition.TableDefinitionBuilder finalBuilder = definitionBuilder;
        if (!columns.isEmpty()) {
            finalBuilder = finalBuilder.columns(columns);
        }
        CreateTableStatement statement = new CreateTableStatement(finalBuilder.build());
        return statement.accept(sqlRenderer, new AstContext());
    }
}
