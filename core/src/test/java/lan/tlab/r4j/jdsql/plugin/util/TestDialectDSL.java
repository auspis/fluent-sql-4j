package lan.tlab.r4j.jdsql.plugin.util;

import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;

public class TestDialectDSL extends DSL {

    public TestDialectDSL(PreparedStatementSpecFactory specFactory) {
        super(specFactory);
    }
}
