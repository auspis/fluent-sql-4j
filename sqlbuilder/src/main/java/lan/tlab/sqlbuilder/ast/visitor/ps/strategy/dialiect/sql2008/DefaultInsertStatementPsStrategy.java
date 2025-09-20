package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.item.InsertData.InsertValues;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.statement.InsertStatement;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.InsertStatementPsStrategy;

public class DefaultInsertStatementPsStrategy implements InsertStatementPsStrategy {
    @Override
    public PsDto handle(InsertStatement stmt, Visitor<PsDto> visitor, AstContext ctx) {
        // Table name
        Table table = (Table) stmt.getTable();
        String tableName = table.getName();
        // Column names
        List<String> columns = stmt.getColumns().stream()
                .map(ColumnReference::getColumn)
                .map(name -> "\"" + name + "\"")
                .collect(Collectors.toList());
        String columnList = String.join(", ", columns);
        // Placeholders and parameters
        String placeholders = "";
        List<Object> params = new ArrayList<>();
        if (stmt.getData() instanceof InsertValues values) {
            placeholders = values.getValueExpressions().stream()
                    .map(val -> {
                        if (val instanceof Literal<?> literal) {
                            params.add(literal.getValue());
                        } else {
                            params.add(null); // fallback
                        }
                        return "?";
                    })
                    .collect(Collectors.joining(", "));
        }
        String sql = "INSERT INTO \"" + tableName + "\" (" + columnList + ") VALUES (" + placeholders + ")";
        return new PsDto(sql, params);
    }
}
