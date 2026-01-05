package io.github.auspis.fluentsql4j.plugin.builtin.postgre;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating PostgreSQL AstToPreparedStatementSpecVisitor instances.
 * <p>
 * Provides convenient access to PostgreSQL AST visitors for tests without boilerplate.
 * This class is specific to the PostgreSQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class PostgreSqlAstToPreparedStatementSpecVisitorFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private PostgreSqlAstToPreparedStatementSpecVisitorFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link AstToPreparedStatementSpecVisitor} for PostgreSQL 15.x dialect.
     *
     * @return AstToPreparedStatementSpecVisitor configured for PostgreSQL 15.x
     * @throws IllegalStateException if the PostgreSQL plugin is not available
     */
    public static AstToPreparedStatementSpecVisitor create() {
        return REGISTRY.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, PostgreSqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .astVisitor();
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (SQL + PreparedStatement) for PostgreSQL.
     *
     * @return PreparedStatementSpecFactory configured for PostgreSQL 15.x
     * @throws IllegalArgumentException if the PostgreSQL plugin is not available
     */
    public static PreparedStatementSpecFactory dialectRendererPostgreSql() {
        return REGISTRY.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, PostgreSqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }

    /**
     * Creates a {@link DSL} instance configured for PostgreSQL 15.x.
     *
     * @return DSL instance configured for PostgreSQL 15.x
     * @throws IllegalStateException if the PostgreSQL plugin is not available
     */
    public static DSL dslPostgreSql() {
        return new DSL(dialectRendererPostgreSql());
    }
}
