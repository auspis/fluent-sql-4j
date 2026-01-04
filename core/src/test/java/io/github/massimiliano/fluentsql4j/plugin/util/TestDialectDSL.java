package io.github.massimiliano.fluentsql4j.plugin.util;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.dsl.DSL;

public class TestDialectDSL extends DSL {

    public TestDialectDSL(PreparedStatementSpecFactory specFactory) {
        super(specFactory);
    }
}
