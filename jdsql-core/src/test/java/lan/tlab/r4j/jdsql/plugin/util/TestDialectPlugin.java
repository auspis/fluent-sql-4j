package lan.tlab.r4j.jdsql.plugin.util;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;

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
                PreparedStatementRenderer.builder().build());
    }
}
