package io.github.auspis.fluentsql4j.ast.visitor;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import java.util.Objects;

/**
 * Factory for creating {@link PreparedStatementSpec} instances from SQL statements.
 * <p>
 * This class coordinates two-phase rendering:
 * <ol>
 *   <li><b>Context preparation</b>: Analyzes the AST to enrich context with metadata (e.g., JOIN detection)</li>
 *   <li><b>SQL generation</b>: Delegates to {@link AstToPreparedStatementSpecVisitor} for dialect-specific rendering</li>
 * </ol>
 * <p>
 * This separation ensures rendering strategies receive the necessary context without managing it themselves.
 * <p>
 * <b>Example usage:</b>
 * <pre>{@code
 * AstToPreparedStatementSpecVisitor astToPsSpecVisitor = AstToPreparedStatementSpecVisitor.builder()
 *     .escapeStrategy(new MysqlEscapeStrategy())
 *     .build();
 * PreparedStatementSpecFactory specFactory = new PreparedStatementSpecFactory(astToPsSpecVisitor);
 *
 * // Create PreparedStatementSpec
 * PreparedStatementSpec spec = specFactory.create(selectStatement);
 * String sql = spec.sql();
 * List<Object> params = spec.parameters();
 * }</pre>
 *
 * @param astVisitor the AstToPreparedStatementSpecVisitor astVisitor for this dialect
 * @param buildHookFactory factory that creates a build hook for each create invocation
 * @since 1.0
 */
public record PreparedStatementSpecFactory(
        AstToPreparedStatementSpecVisitor astVisitor, BuildHookFactory buildHookFactory) {

    private static final ContextPreparationVisitor CONTEXT_ANALYZER = new ContextPreparationVisitor();

    public PreparedStatementSpecFactory {
        Objects.requireNonNull(astVisitor, "AstToPreparedStatementSpecVisitor must not be null");
        buildHookFactory = (buildHookFactory == null) ? BuildHookFactory.nullObject() : buildHookFactory;
    }

    /**
     * Backward-compatible constructor using a no-op hook factory.
     *
     * @param astVisitor the AstToPreparedStatementSpecVisitor astVisitor for this dialect
     */
    public PreparedStatementSpecFactory(AstToPreparedStatementSpecVisitor astVisitor) {
        this(astVisitor, BuildHookFactory.nullObject());
    }

    /**
     * Creates a PreparedStatementSpec from a SQL statement.
     *
     * @param statement the statement to render
     * @return the PreparedStatementSpec with SQL and parameters
     */
    public PreparedStatementSpec create(Statement statement) {
        BuildHook hook = BuildHook.nullObject();
        try {
            hook = buildHookFactory.create();
            hook.before(statement);
            AstContext enrichedCtx = statement.accept(CONTEXT_ANALYZER, new AstContext());
            PreparedStatementSpec result = statement.accept(astVisitor, enrichedCtx);
            hook.onSuccess(result);
            return result;
        } catch (Throwable t) {
            hook.onError(t);
            throw t;
        }
    }
}
