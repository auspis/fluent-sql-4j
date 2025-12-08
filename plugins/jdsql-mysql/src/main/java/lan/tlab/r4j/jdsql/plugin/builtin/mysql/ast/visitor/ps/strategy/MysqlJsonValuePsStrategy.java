package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonValue;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonValuePsStrategy;

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
    public PreparedStatementSpec handle(JsonValue jsonValue, PreparedStatementRenderer renderer, AstContext ctx) {
        var documentResult = jsonValue.jsonDocument().accept(renderer, ctx);
        var pathResult = jsonValue.path().accept(renderer, ctx);

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
