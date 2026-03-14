package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.dml.statement.TruncateStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlTruncateStatementPsStrategyTest {

    @Test
    void truncate() {
        TruncateStatement stmt =
                TruncateStatement.builder().table(new TableIdentifier("users")).build();

        AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
        PreparedStatementSpec ps = specFactory.visit(stmt, new AstContext());

        Assertions.assertThat(ps.sql()).isEqualTo("TRUNCATE TABLE \"users\"");
        Assertions.assertThat(ps.parameters()).isEmpty();
    }
}
