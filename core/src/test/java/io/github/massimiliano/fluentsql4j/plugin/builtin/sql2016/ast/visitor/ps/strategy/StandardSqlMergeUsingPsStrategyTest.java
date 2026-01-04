package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlMergeUsingPsStrategyTest {

    private StandardSqlMergeUsingPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlMergeUsingPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void tableSource() {
        TableIdentifier sourceTable = new TableIdentifier("source_table", "src");
        MergeUsing mergeUsing = new MergeUsing(sourceTable);

        PreparedStatementSpec result = strategy.handle(mergeUsing, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"source_table\" AS src");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void tableSourceWithoutAlias() {
        TableIdentifier sourceTable = new TableIdentifier("staging");
        MergeUsing mergeUsing = new MergeUsing(sourceTable);

        PreparedStatementSpec result = strategy.handle(mergeUsing, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"staging\"");
        assertThat(result.parameters()).isEmpty();
    }
}
