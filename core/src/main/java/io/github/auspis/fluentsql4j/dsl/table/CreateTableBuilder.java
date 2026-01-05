package io.github.auspis.fluentsql4j.dsl.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ColumnDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.DataType;
import io.github.auspis.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ReferencesItem;
import io.github.auspis.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.auspis.fluentsql4j.ast.ddl.statement.CreateTableStatement;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.dsl.util.PsUtil;

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

    /**
     * Adds a PRIMARY KEY constraint with the specified column names.
     *
     * @param columnNames the names of columns to include in the primary key
     * @return this builder instance
     * @throws IllegalArgumentException if columnNames is empty
     */
    public CreateTableBuilder primaryKey(String... columnNames) {
        if (columnNames == null || columnNames.length == 0) {
            throw new IllegalArgumentException("Column names cannot be empty in primaryKey()");
        }
        definitionBuilder = definitionBuilder.primaryKey(new PrimaryKeyDefinition(List.of(columnNames)));
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

    /**
     * Adds an INDEX constraint with the specified name and columns.
     *
     * @param indexName the name of the index (cannot be null)
     * @param columns the names of columns to include in the index (cannot be empty)
     * @return this builder instance
     * @throws IllegalArgumentException if indexName is null or columns is empty
     */
    public CreateTableBuilder index(String indexName, String... columns) {
        if (indexName == null) {
            throw new IllegalArgumentException("Index name cannot be null");
        }
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("Column names cannot be empty in index()");
        }
        definitionBuilder = definitionBuilder.index(new IndexDefinition(indexName, columns));
        return this;
    }

    /**
     * Adds a UNIQUE constraint with the specified columns.
     *
     * @param columns the names of columns to include in the unique constraint (cannot be empty)
     * @return this builder instance
     * @throws IllegalArgumentException if columns is empty
     */
    public CreateTableBuilder unique(String... columns) {
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("Column names cannot be empty in unique()");
        }
        definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.UniqueConstraintDefinition(columns));
        return this;
    }

    /**
     * Adds a foreign key constraint on the specified column referencing another table.
     *
     * @param column the column name to which the foreign key applies (cannot be null)
     * @param refTable the referenced table name (cannot be null)
     * @param refColumns the referenced column names (at least one required)
     * @return this builder instance
     * @throws IllegalArgumentException if column or refTable is null, or refColumns is empty
     */
    public CreateTableBuilder foreignKey(String column, String refTable, String... refColumns) {
        if (column == null) {
            throw new IllegalArgumentException("Column name cannot be null in foreignKey()");
        }
        if (refTable == null) {
            throw new IllegalArgumentException("Referenced table name cannot be null in foreignKey()");
        }
        if (refColumns == null || refColumns.length == 0) {
            throw new IllegalArgumentException("Referenced column names cannot be empty in foreignKey()");
        }
        definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.ForeignKeyConstraintDefinition(
                List.of(column), new ReferencesItem(refTable, refColumns)));
        return this;
    }

    /**
     * Adds a CHECK constraint with the specified predicate expression.
     *
     * @param expr the predicate to check (cannot be null)
     * @return this builder instance
     * @throws IllegalArgumentException if expr is null
     */
    public CreateTableBuilder check(Predicate expr) {
        if (expr == null) {
            throw new IllegalArgumentException("Predicate cannot be null in check()");
        }
        definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.CheckConstraintDefinition(expr));
        return this;
    }

    /**
     * Adds a DEFAULT constraint with the specified scalar expression value.
     *
     * @param value the default value expression (cannot be null)
     * @return this builder instance
     * @throws IllegalArgumentException if value is null
     */
    public CreateTableBuilder defaultConstraint(ScalarExpression value) {
        if (value == null) {
            throw new IllegalArgumentException("Default value expression cannot be null in defaultConstraint()");
        }
        definitionBuilder = definitionBuilder.constraint(new ConstraintDefinition.DefaultConstraintDefinition(value));
        return this;
    }

    /**
     * Marks the specified column with a NOT NULL constraint.
     *
     * @param columnName the name of the column to mark as NOT NULL (cannot be null)
     * @return this builder instance
     * @throws IllegalArgumentException if columnName is null, empty, or column does not exist
     */
    public CreateTableBuilder notNullColumn(String columnName) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty in notNullColumn()");
        }
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

    public PreparedStatement build(Connection connection) throws SQLException {
        TableDefinition.TableDefinitionBuilder finalBuilder = definitionBuilder;
        if (!columns.isEmpty()) {
            finalBuilder = finalBuilder.columns(columns);
        }
        CreateTableStatement statement = new CreateTableStatement(finalBuilder.build());
        PreparedStatementSpec spec = specFactory.create(statement);
        return PsUtil.preparedStatement(spec, connection);
    }
}
