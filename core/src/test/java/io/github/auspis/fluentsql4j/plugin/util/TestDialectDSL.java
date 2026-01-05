package io.github.auspis.fluentsql4j.plugin.util;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;

public class TestDialectDSL extends DSL {

    public TestDialectDSL(PreparedStatementSpecFactory specFactory) {
        super(specFactory);
    }
}
