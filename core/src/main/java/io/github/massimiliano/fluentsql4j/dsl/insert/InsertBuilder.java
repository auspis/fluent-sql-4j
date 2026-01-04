package io.github.massimiliano.fluentsql4j.dsl.insert;

import io.github.massimiliano.fluentsql4j.ast.core.expression.Expression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.massimiliano.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.massimiliano.fluentsql4j.ast.dml.statement.InsertStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.dsl.util.LiteralUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InsertBuilder {
    private final PreparedStatementSpecFactory specFactory;
    private TableIdentifier table;
    private final List<ColumnReference> columns = new ArrayList<>();
    private InsertData data = new DefaultValues();

    public InsertBuilder(PreparedStatementSpecFactory specFactory, String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.specFactory = specFactory;
        this.table = new TableIdentifier(tableName);
    }

    public InsertBuilder defaultValues() {
        this.data = new DefaultValues();
        return this;
    }

    public InsertBuilder set(String columnName, String value) {
        return setValue(columnName, value);
    }

    public InsertBuilder set(String columnName, Number value) {
        return setValue(columnName, value);
    }

    public InsertBuilder set(String columnName, Boolean value) {
        return setValue(columnName, value);
    }

    public InsertBuilder set(String columnName, LocalDate value) {
        return setValue(columnName, value);
    }

    public InsertBuilder set(String columnName, LocalDateTime value) {
        return setValue(columnName, value);
    }

    private List<Expression> getOrCreateExpressionList() {
        if (data instanceof InsertValues insertValues) {
            return new ArrayList<>(insertValues.valueExpressions());
        }
        return new ArrayList<>();
    }

    private InsertBuilder setValue(String columnName, Object value) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        columns.add(ColumnReference.of(table.name(), columnName));

        List<Expression> expressions = getOrCreateExpressionList();
        expressions.add(value == null ? Literal.ofNull() : LiteralUtil.createLiteral(value));
        this.data = new InsertValues(expressions);
        return this;
    }

    public PreparedStatement build(Connection connection) throws SQLException {
        validateState();
        InsertStatement statement = getCurrentStatement();
        PreparedStatementSpec result = specFactory.create(statement);

        PreparedStatement ps = connection.prepareStatement(result.sql());
        for (int i = 0; i < result.parameters().size(); i++) {
            ps.setObject(i + 1, result.parameters().get(i));
        }
        return ps;
    }

    private void validateState() {
        if (table == null) {
            throw new IllegalStateException("Table must be specified");
        }
        if (!columns.isEmpty() && data instanceof DefaultValues) {
            throw new IllegalStateException("Columns specified but no values provided");
        }
        if (data instanceof InsertValues insertValues) {
            if (!columns.isEmpty()
                    && columns.size() != insertValues.valueExpressions().size()) {
                throw new IllegalStateException(
                        "Number of columns (" + columns.size() + ") does not match number of values ("
                                + insertValues.valueExpressions().size() + ")");
            }
        }
    }

    private InsertStatement getCurrentStatement() {
        return InsertStatement.builder()
                .table(table)
                .columns(columns)
                .data(data)
                .build();
    }
}
