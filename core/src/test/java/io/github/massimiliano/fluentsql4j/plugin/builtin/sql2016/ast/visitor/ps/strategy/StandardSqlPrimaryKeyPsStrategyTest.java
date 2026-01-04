package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlPrimaryKeyPsStrategyTest {

    private final PrimaryKeyPsStrategy strategy = new StandardSqlPrimaryKeyPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void singleColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("id");

        PreparedStatementSpec result = strategy.handle(primaryKey, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multiColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("user_id", "account_id");

        PreparedStatementSpec result = strategy.handle(primaryKey, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user_id\", \"account_id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void threeColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("year", "month", "day");

        PreparedStatementSpec result = strategy.handle(primaryKey, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"year\", \"month\", \"day\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithListConstructor() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition(List.of("column1", "column2"));

        PreparedStatementSpec result = strategy.handle(primaryKey, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"column1\", \"column2\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithSpecialCharacters() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("user-id", "created_at");

        PreparedStatementSpec result = strategy.handle(primaryKey, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user-id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }
}
