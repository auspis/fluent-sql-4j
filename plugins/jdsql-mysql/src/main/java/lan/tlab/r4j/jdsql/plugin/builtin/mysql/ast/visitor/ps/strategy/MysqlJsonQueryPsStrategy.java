package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonQueryPsStrategy;

/**
 * MySQL-specific implementation of JsonQueryPsStrategy.
 * <p>
 * MySQL does not support the SQL:2016 standard JSON_QUERY function.
 * Instead, it uses JSON_EXTRACT to extract JSON objects or arrays.
 * <p>
 * <b>Standard SQL:2016:</b>
 * <pre>{@code
 * JSON_QUERY(doc, '$.path')
 * }</pre>
 * <p>
 * <b>MySQL 8.0 equivalent:</b>
 * <pre>{@code
 * JSON_EXTRACT(doc, '$.path')
 * }</pre>
 * <p>
 * Unlike JSON_VALUE (which extracts scalars), JSON_QUERY extracts JSON objects
 * or arrays. MySQL's JSON_EXTRACT handles both cases, returning the value with
 * JSON formatting preserved.
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>MySQL does not support RETURNING data_type - this is ignored</li>
 *   <li>MySQL does not support wrapper behavior (WITH/WITHOUT WRAPPER) - this is ignored</li>
 *   <li>MySQL does not support ON EMPTY behavior - this is ignored</li>
 *   <li>MySQL does not support ON ERROR behavior - this is ignored</li>
 * </ul>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-extract">MySQL JSON_EXTRACT</a>
 */
public class MysqlJsonQueryPsStrategy implements JsonQueryPsStrategy {

    @Override
    public PreparedStatementSpec handle(JsonQuery jsonQuery, PreparedStatementRenderer renderer, AstContext ctx) {
        var documentResult = jsonQuery.jsonDocument().accept(renderer, ctx);
        var pathResult = jsonQuery.path().accept(renderer, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        // MySQL syntax: JSON_EXTRACT(doc, path)
        StringBuilder sql = new StringBuilder("JSON_EXTRACT(");
        sql.append(documentResult.sql());
        sql.append(", ");
        sql.append(pathResult.sql());
        sql.append(")");

        // Note: RETURNING, wrapper behavior, ON EMPTY, and ON ERROR are ignored
        // MySQL does not support these SQL:2016 features

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
