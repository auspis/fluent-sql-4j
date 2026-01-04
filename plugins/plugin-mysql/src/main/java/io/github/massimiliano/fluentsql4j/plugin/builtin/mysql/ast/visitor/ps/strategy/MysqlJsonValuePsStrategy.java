package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.JsonValue;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.JsonValuePsStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL-specific implementation of JsonValuePsStrategy.
 * <p>
 * MySQL does not support the SQL:2016 standard JSON_VALUE function.
 * Instead, it uses JSON_EXTRACT to extract values, wrapped in JSON_UNQUOTE
 * to remove JSON quotes from scalar values.
 * <p>
 * <b>Standard SQL:2016:</b>
 * <pre>{@code
 * JSON_VALUE(doc, '$.path')
 * }</pre>
 * <p>
 * <b>MySQL 8.0 equivalent:</b>
 * <pre>{@code
 * JSON_UNQUOTE(JSON_EXTRACT(doc, '$.path'))
 * }</pre>
 * <p>
 * JSON_EXTRACT returns the value with JSON formatting (e.g., "string" with quotes),
 * so JSON_UNQUOTE is needed to get the raw scalar value.
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>MySQL does not support RETURNING data_type - this is ignored</li>
 *   <li>MySQL does not support ON EMPTY behavior - this is ignored</li>
 *   <li>MySQL does not support ON ERROR behavior - this is ignored</li>
 *   <li>Type conversion must be done with CAST if needed</li>
 * </ul>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-extract">MySQL JSON_EXTRACT</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-modification-functions.html#function_json-unquote">MySQL JSON_UNQUOTE</a>
 */
public class MysqlJsonValuePsStrategy implements JsonValuePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            JsonValue jsonValue, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var documentResult = jsonValue.jsonDocument().accept(astToPsSpecVisitor, ctx);
        var pathResult = jsonValue.path().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        // MySQL syntax: JSON_UNQUOTE(JSON_EXTRACT(doc, path))
        StringBuilder sql = new StringBuilder("JSON_UNQUOTE(JSON_EXTRACT(");
        sql.append(documentResult.sql());
        sql.append(", ");
        sql.append(pathResult.sql());
        sql.append("))");

        // Note: RETURNING, ON EMPTY, and ON ERROR are ignored - MySQL does not support them
        // If type conversion is needed, users should use CAST in the outer query

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
