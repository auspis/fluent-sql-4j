package io.github.massimiliano.fluentsql4j.plugin.util;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.dsl.DSL;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;

public final class TestDialectPlugin {

    public static final String OTHER_DIALECT_NAME = "other-dialect";

    public static final String DIALECT_NAME = "test-dialect";
    public static final String DIALECT_VERSION = "3.4.5";

    private static final SqlDialectPlugin INSTANCE =
            new SqlDialectPlugin(DIALECT_NAME, DIALECT_VERSION, TestDialectPlugin::createDSL);

    private TestDialectPlugin() {}

    public static SqlDialectPlugin instance() {
        return INSTANCE;
    }

    private static DSL createDSL() {
        return new TestDialectDSL(createPreparedStatementSpecFactory());
    }

    private static PreparedStatementSpecFactory createPreparedStatementSpecFactory() {
        return new PreparedStatementSpecFactory(
                AstToPreparedStatementSpecVisitor.builder().build());
    }
}
