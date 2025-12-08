package lan.tlab.r4j.jdsql.ast.visitor;

import java.util.Objects;
import lan.tlab.r4j.jdsql.ast.core.statement.Statement;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

/**
 * Factory for creating {@link PreparedStatementSpec} instances from SQL statements.
 * <p>
 * This class coordinates two-phase rendering:
 * <ol>
 *   <li><b>Context preparation</b>: Analyzes the AST to enrich context with metadata (e.g., JOIN detection)</li>
 *   <li><b>SQL generation</b>: Delegates to {@link PreparedStatementRenderer} for dialect-specific rendering</li>
 * </ol>
 * <p>
 * This separation ensures rendering strategies receive the necessary context without managing it themselves.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * PreparedStatementRenderer psRenderer = PreparedStatementRenderer.builder()
 *     .escapeStrategy(new MysqlEscapeStrategy())
 *     .build();
 * PreparedStatementSpecFactory specFactory = new PreparedStatementSpecFactory(psRenderer);
 *
 * // Create PreparedStatementSpec
 * PreparedStatementSpec spec = specFactory.create(selectStatement);
 * String sql = spec.sql();
 * List<Object> params = spec.parameters();
 * }</pre>
 *
 * @param psRenderer the PreparedStatement renderer for this dialect
 * @since 1.0
 */
public record PreparedStatementSpecFactory(PreparedStatementRenderer psRenderer) {

    private static final ContextPreparationVisitor CONTEXT_ANALYZER = new ContextPreparationVisitor();

    public PreparedStatementSpecFactory {
        Objects.requireNonNull(psRenderer, "PreparedStatementRenderer must not be null");
    }

    /**
     * Creates a PreparedStatementSpec from a SQL statement.
     *
     * @param statement the statement to render
     * @return the PreparedStatementSpec with SQL and parameters
     */
    public PreparedStatementSpec create(Statement statement) {
        AstContext enrichedCtx = statement.accept(CONTEXT_ANALYZER, new AstContext());
        return statement.accept(psRenderer, enrichedCtx);
    }
}
