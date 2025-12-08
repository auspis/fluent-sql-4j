package lan.tlab.r4j.jdsql.ast.visitor;

import java.util.Objects;
import lan.tlab.r4j.jdsql.ast.statement.Statement;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

/**
 * Orchestrates SQL statement rendering for a specific dialect.
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
 * DialectRenderer renderer = new DialectRenderer(psRenderer);
 *
 * // Render prepared statement DTO
 * PsDto psDto = renderer.renderPreparedStatement(selectStatement);
 * String sql = psDto.sql();
 * List<Object> params = psDto.parameters();
 * }</pre>
 *
 * @param psRenderer the PreparedStatement renderer for this dialect
 * @since 1.0
 */
public record DialectRenderer(PreparedStatementRenderer psRenderer) {

    private static final ContextPreparationVisitor CONTEXT_ANALYZER = new ContextPreparationVisitor();

    public DialectRenderer {
        Objects.requireNonNull(psRenderer, "PreparedStatementRenderer must not be null");
    }

    /**
     * Renders a statement as PreparedStatement DTO.
     *
     * @param statement the statement to render
     * @return the PreparedStatement DTO with SQL and parameters
     */
    public PsDto renderPreparedStatement(Statement statement) {
        AstContext enrichedCtx = statement.accept(CONTEXT_ANALYZER, new AstContext());
        return statement.accept(psRenderer, enrichedCtx);
    }
}
