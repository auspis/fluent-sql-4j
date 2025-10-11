package lan.tlab.r4j.sql.dsl.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.util.LiteralUtil;

public class InsertBuilder {
    private final SqlRenderer sqlRenderer;
    private TableIdentifier table;
    private final List<ColumnReference> columns = new ArrayList<>();
    private InsertData data = new DefaultValues();

    public InsertBuilder(SqlRenderer sqlRenderer, String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("Table name cannot be null or empty");
        }
        this.sqlRenderer = sqlRenderer;
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
            return new ArrayList<>(insertValues.getValueExpressions());
        }
        return new ArrayList<>();
    }

    private InsertBuilder setValue(String columnName, Object value) {
        if (columnName == null || columnName.trim().isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        columns.add(ColumnReference.of(table.getName(), columnName));

        List<Expression> expressions = getOrCreateExpressionList();
        expressions.add(value == null ? Literal.ofNull() : LiteralUtil.createLiteral(value));
        this.data = new InsertValues(expressions);
        return this;
    }

    public String build() {
        validateState();
        InsertStatement statement = getCurrentStatement();
        return statement.accept(sqlRenderer, new AstContext());
    }

    public PreparedStatement buildPreparedStatement(Connection connection) throws SQLException {
        validateState();
        InsertStatement stmt = getCurrentStatement();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto result = stmt.accept(visitor, new AstContext());

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
                    && columns.size() != insertValues.getValueExpressions().size()) {
                throw new IllegalStateException(
                        "Number of columns (" + columns.size() + ") does not match number of values ("
                                + insertValues.getValueExpressions().size() + ")");
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
