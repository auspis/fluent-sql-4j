package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.dml.InsertStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.InsertStatementPsStrategy;

public class DefaultInsertStatementPsStrategy implements InsertStatementPsStrategy {
    @Override
    public PsDto handle(InsertStatement stmt, Visitor<PsDto> renderer, AstContext ctx) {
        // TableIdentifier name
        TableIdentifier table = (TableIdentifier) stmt.getTable();
        String tableName = table.name();

        InsertData data = stmt.getData();

        if (data instanceof DefaultValues defaultValues) {
            // Handle DEFAULT VALUES case
            PsDto dataResult = defaultValues.accept(renderer, ctx);
            String sql = "INSERT INTO \"" + tableName + "\" " + dataResult.sql();
            return new PsDto(sql, dataResult.parameters());
        } else if (data instanceof InsertValues values) {
            // Handle explicit values case
            // Column names
            List<String> columns = stmt.getColumns().stream()
                    .map(ColumnReference::getColumn)
                    .map(name -> "\"" + name + "\"")
                    .collect(Collectors.toList());
            String columnList = String.join(", ", columns);

            // Placeholders and parameters
            List<Object> params = new ArrayList<>();
            String placeholders = values.getValueExpressions().stream()
                    .map(val -> {
                        if (val instanceof Literal<?> literal) {
                            params.add(literal.getValue());
                        } else {
                            params.add(null); // fallback
                        }
                        return "?";
                    })
                    .collect(Collectors.joining(", "));

            String sql = "INSERT INTO \"" + tableName + "\" (" + columnList + ") VALUES (" + placeholders + ")";
            return new PsDto(sql, params);
        } else {
            throw new UnsupportedOperationException("Unsupported InsertData type: " + data.getClass());
        }
    }
}
