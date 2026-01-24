package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedDelete;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.WhenMatchedDeletePsStrategy;

/**
 * MySQL-specific strategy for WHEN MATCHED THEN DELETE in MERGE statements.
 * MySQL does not support DELETE in MERGE...INSERT...ON DUPLICATE KEY UPDATE syntax.
 * This strategy throws UnsupportedOperationException to indicate the limitation.
 */
public class MySqlWhenMatchedDeletePsStrategy implements WhenMatchedDeletePsStrategy {

    @Override
    public PreparedStatementSpec handle(
            WhenMatchedDelete item, AstToPreparedStatementSpecVisitor visitor, AstContext ctx) {
        throw new UnsupportedOperationException("MySQL does not support WHEN MATCHED THEN DELETE in MERGE statements. "
                + "MERGE is translated to INSERT...ON DUPLICATE KEY UPDATE which does not support DELETE.");
    }
}
