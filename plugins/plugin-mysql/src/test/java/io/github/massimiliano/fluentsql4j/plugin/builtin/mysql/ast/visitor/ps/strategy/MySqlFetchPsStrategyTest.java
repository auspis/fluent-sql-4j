package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.dql.clause.Fetch;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlAstToPreparedStatementSpecVisitorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlFetchPsStrategyTest {

    private MySqlFetchPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor astToPsSpecVisitor;

    @BeforeEach
    public void setUp() {
        strategy = new MySqlFetchPsStrategy();
        astToPsSpecVisitor = MysqlAstToPreparedStatementSpecVisitorFactory.create();
    }

    @Test
    void empty() {
        Fetch pagination = Fetch.nullObject();

        PreparedStatementSpec result = strategy.handle(pagination, astToPsSpecVisitor, new AstContext());
        assertThat(result.sql()).isEqualTo("");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void ok() {
        Fetch pagination = new Fetch(0, 10);

        PreparedStatementSpec result = strategy.handle(pagination, astToPsSpecVisitor, new AstContext());
        assertThat(result.sql()).isEqualTo("LIMIT 10 OFFSET 0");
        assertThat(result.parameters()).isEmpty();

        pagination = new Fetch(16, 8);
        result = strategy.handle(pagination, astToPsSpecVisitor, new AstContext());
        assertThat(result.sql()).isEqualTo("LIMIT 8 OFFSET 16");
        assertThat(result.parameters()).isEmpty();
    }
}
