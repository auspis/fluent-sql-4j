package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginRegistry;

/**
 * Test utility factory for creating MySQL AstToPreparedStatementSpecVisitor instances.
 * <p>
 * Provides convenient access to MySQL AST visitors for tests without boilerplate.
 * This class is specific to the MySQL plugin module and uses the plugin's constants.
 *
 * @since 1.0
 */
public final class MysqlAstToPreparedStatementSpecVisitorFactory {

    private static final SqlDialectPluginRegistry REGISTRY = SqlDialectPluginRegistry.createWithServiceLoader();

    private MysqlAstToPreparedStatementSpecVisitorFactory() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a {@link AstToPreparedStatementSpecVisitor} for MySQL 8.x dialect.
     *
     * @return AstToPreparedStatementSpecVisitor configured for MySQL 8.x
     * @throws IllegalStateException if the MySQL plugin is not available
     */
    public static AstToPreparedStatementSpecVisitor create() {
        return REGISTRY.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow()
                .astVisitor();
    }

    /**
     * Creates a complete {@link PreparedStatementSpecFactory} (PreparedStatement) for MySQL.
     *
     * @return PreparedStatementSpecFactory configured for MySQL 8.x
     * @throws IllegalArgumentException if the MySQL plugin is not available
     */
    public static PreparedStatementSpecFactory preparedStatementSpecFactory() {
        return REGISTRY.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
    }
}
