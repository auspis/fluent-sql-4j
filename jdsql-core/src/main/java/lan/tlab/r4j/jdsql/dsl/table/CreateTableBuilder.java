package lan.tlab.r4j.jdsql.dsl.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ColumnDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.statement.CreateTableStatement;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.dsl.util.PsUtil;

public class CreateTableBuilder {

    private TableDefinition.TableDefinitionBuilder definitionBuilder;
    private List<ColumnDefinition> columns = new ArrayList<>();
    private PreparedStatementSpecFactory specFactory;

    public CreateTableBuilder(PreparedStatementSpecFactory specFactory, String tableName) {
        this.specFactory = specFactory;
        this.definitionBuilder = TableDefinition.builder().name(tableName);
    }

    public ColumnBuilder column(String columnName) {
        return new ColumnBuilder(this, columnName);
    }

    public CreateTableBuilder primaryKey(String... columnNames) {
        if (columnNames.length > 0) {
            definitionBuilder = definitionBuilder.primaryKey(new PrimaryKeyDefinition(List.of(columnNames)));
        }
        return this;
    }

    public CreateTableBuilder columnIntegerPrimaryKey(String columnName) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.integer())
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        addColumn(columnDef);
        return primaryKey(columnName);
    }

    public CreateTableBuilder columnStringPrimaryKey(String columnName, int length) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.varchar(length))
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        addColumn(columnDef);
        return primaryKey(columnName);
    }

    public CreateTableBuilder columnTimestampNotNull(String columnName) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.timestamp())
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        addColumn(columnDef);
        return this;
    }

    public CreateTableBuilder columnVarcharNotNull(String columnName, int length) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.varchar(length))
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        addColumn(columnDef);
        return this;
    }

    public CreateTableBuilder columnDecimalNotNull(String columnName, int precision, int scale) {
        ColumnDefinition columnDef = ColumnDefinition.builder()
                .name(columnName)
                .type(DataType.decimal(precision, scale))
                .notNullConstraint(new NotNullConstraintDefinition())
                .build();

        addColumn(columnDef);
        return this;
    }

    void addColumn(ColumnDefinition column) {
        // replace existing column with same name if present
        columns.removeIf(c -> c.name().equals(column.name()));
        columns.add(column);
    }

    public CreateTableBuilder index(String indexName, String... columns) {
        if (indexName != null && columns != null && columns.length > 0) {
            definitionBuilder = definitionBuilder.index(new IndexDefinition(indexName, columns));
        }
        return this;
    }

    public CreateTableBuilder unique(String... columns) {
        if (columns != null && columns.length > 0) {
            definitionBuilder =
                    definitionBuilder.constraint(new ConstraintDefinition.UniqueConstraintDefinition(columns));
        }
        return this;
    }

    /**
     * Convenience method to add a single-column foreign key constraint.
     */
    public CreateTableBuilder foreignKey(String column, String refTable, String... refColumns) {
        if (column != null && refTable != null) {
            definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.ForeignKeyConstraintDefinition(
                    List.of(column), new ReferencesItem(refTable, refColumns)));
        }
        return this;
    }

    public CreateTableBuilder check(Predicate expr) {
        if (expr != null) {
            definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.CheckConstraintDefinition(expr));
        }
        return this;
    }

    public CreateTableBuilder defaultConstraint(ScalarExpression value) {
        if (value != null) {
            definitionBuilder =
                    definitionBuilder.constraint(new ConstraintDefinition.DefaultConstraintDefinition(value));
        }
        return this;
    }

    public CreateTableBuilder notNullColumn(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            ColumnDefinition c = columns.get(i);
            if (c.name().equals(columnName)) {
                ColumnDefinition updated = ColumnDefinition.builder()
                        .name(c.name())
                        .type(c.type())
                        .notNullConstraint(new NotNullConstraintDefinition())
                        .defaultConstraint(c.defaultConstraint())
                        .build();

                columns.set(i, updated);
                // columns list updated; will be applied once during build()
                return this;
            }
        }
        throw new IllegalArgumentException("Column not found: " + columnName);
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        TableDefinition.TableDefinitionBuilder finalBuilder = definitionBuilder;
        if (!columns.isEmpty()) {
            finalBuilder = finalBuilder.columns(columns);
        }
        CreateTableStatement statement = new CreateTableStatement(finalBuilder.build());
        PreparedStatementSpec spec = specFactory.create(statement);
        return PsUtil.preparedStatement(spec, connection);
    }
}
