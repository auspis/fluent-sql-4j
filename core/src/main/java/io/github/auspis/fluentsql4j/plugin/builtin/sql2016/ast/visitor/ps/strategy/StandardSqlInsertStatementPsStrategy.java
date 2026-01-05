package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.DefaultValues;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.auspis.fluentsql4j.ast.dml.statement.InsertStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.InsertStatementPsStrategy;

public class StandardSqlInsertStatementPsStrategy implements InsertStatementPsStrategy {
    @Override
    public PreparedStatementSpec handle(InsertStatement stmt, Visitor<PreparedStatementSpec> renderer, AstContext ctx) {
        // TableIdentifier name
        TableIdentifier table = (TableIdentifier) stmt.table();
        String tableName = table.name();

        InsertData data = stmt.data();

        if (data instanceof DefaultValues defaultValues) {
            // Handle DEFAULT VALUES case
            PreparedStatementSpec dataResult = defaultValues.accept(renderer, ctx);
            String sql = "INSERT INTO \"" + tableName + "\" " + dataResult.sql();
            return new PreparedStatementSpec(sql, dataResult.parameters());
        } else if (data instanceof InsertValues values) {
            // Handle explicit values case
            // Column names
            List<String> columns = stmt.columns().stream()
                    .map(ColumnReference::column)
                    .map(name -> "\"" + name + "\"")
                    .collect(Collectors.toList());
            String columnList = String.join(", ", columns);

            // Placeholders and parameters
            List<Object> params = new ArrayList<>();
            String placeholders = values.valueExpressions().stream()
                    .map(val -> {
                        if (val instanceof Literal<?> literal) {
                            params.add(literal.value());
                        } else {
                            params.add(null); // fallback
                        }
                        return "?";
                    })
                    .collect(Collectors.joining(", "));

            String sql = "INSERT INTO \"" + tableName + "\" (" + columnList + ") VALUES (" + placeholders + ")";
            return new PreparedStatementSpec(sql, params);
        } else {
            throw new UnsupportedOperationException("Unsupported InsertData type: " + data.getClass());
        }
    }
}
