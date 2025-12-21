package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.json.JsonExists;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.JsonExistsPsStrategy;

/**
 * MySQL-specific implementation of JsonExistsPsStrategy.
 * <p>
 * MySQL does not support the SQL:2016 standard JSON_EXISTS function.
 * Instead, it uses JSON_CONTAINS_PATH to check if a JSON path exists.
 * <p>
 * <b>Standard SQL:2016:</b>
 * <pre>{@code
 * JSON_EXISTS(doc, '$.path')
 * }</pre>
 * <p>
 * <b>MySQL 8.0 equivalent:</b>
 * <pre>{@code
 * JSON_CONTAINS_PATH(doc, 'one', '$.path')
 * }</pre>
 * <p>
 * The 'one' parameter means "return true if at least one path exists".
 * MySQL also supports 'all' to check if all paths exist.
 * <p>
 * <b>Limitations:</b>
 * <ul>
 *   <li>MySQL does not support ON ERROR behavior - this is ignored</li>
 *   <li>JSON_CONTAINS_PATH returns 1/0 instead of TRUE/FALSE</li>
 * </ul>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-contains-path">MySQL JSON_CONTAINS_PATH</a>
 */
public class MysqlJsonExistsPsStrategy implements JsonExistsPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            JsonExists jsonExists, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var documentResult = jsonExists.jsonDocument().accept(astToPsSpecVisitor, ctx);
        var pathResult = jsonExists.path().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(documentResult.parameters());
        parameters.addAll(pathResult.parameters());

        // MySQL syntax: JSON_CONTAINS_PATH(doc, 'one', path)
        // 'one' means "return true if at least one path exists"
        StringBuilder sql = new StringBuilder("JSON_CONTAINS_PATH(");
        sql.append(documentResult.sql());
        sql.append(", 'one', ");
        sql.append(pathResult.sql());
        sql.append(")");

        // Note: ON ERROR behavior is ignored - MySQL does not support it
        // The function returns 1 if path exists, 0 if not, NULL if invalid

        return new PreparedStatementSpec(sql.toString(), parameters);
    }
}
