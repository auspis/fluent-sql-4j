package lan.tlab.r4j.jdsql.ast.visitor;

import java.util.Objects;
import lan.tlab.r4j.jdsql.ast.statement.Statement;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

/**
 * Encapsulates SQL and PreparedStatement renderers for a specific SQL dialect.
 * <p>
 * This record ensures consistency between SQL generation and prepared statement creation
 * by bundling both renderers together. This guarantees that when building SQL statements
 * and prepared statements, the same dialect-specific rules are applied.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * SqlRenderer sqlRenderer = SqlRenderer.builder().build();
 * PreparedStatementRenderer psRenderer = PreparedStatementRenderer.builder()
 *     .sqlRenderer(sqlRenderer)
 *     .build();
 * DialectRenderer renderer = new DialectRenderer(sqlRenderer, psRenderer);
 *
 * // Render SQL string
 * String sql = renderer.renderSql(selectStatement);
 *
 * // Render prepared statement DTO
 * PsDto psDto = renderer.renderPreparedStatement(insertStatement);
 * }</pre>
 *
 * @param sqlRenderer the SQL renderer for this dialect
 * @param psRenderer the PreparedStatement renderer for this dialect
 * @since 1.0
 */
public record DialectRenderer(SqlRenderer sqlRenderer, PreparedStatementRenderer psRenderer) {

    private static final ContextPreparationVisitor CONTEXT_ANALYZER = new ContextPreparationVisitor();

    /**
     * Compact constructor with validation.
     *
     * @throws NullPointerException if any parameter is {@code null}
     */
    public DialectRenderer {
        Objects.requireNonNull(sqlRenderer, "SqlRenderer must not be null");
        Objects.requireNonNull(psRenderer, "PreparedStatementRenderer must not be null");
    }

    /**
     * Renders a statement as SQL string.
     *
     * @param statement the statement to render
     * @return the SQL string representation
     */
    public String renderSql(Statement statement) {
        AstContext enrichedCtx = statement.accept(CONTEXT_ANALYZER, new AstContext());
        return statement.accept(sqlRenderer, enrichedCtx);
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
