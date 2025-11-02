package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlPrimaryKeyPsStrategyTest {

    private final PrimaryKeyPsStrategy strategy = new StandardSqlPrimaryKeyPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void singleColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("id");

        PsDto result = strategy.handle(primaryKey, renderer, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multiColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("user_id", "account_id");

        PsDto result = strategy.handle(primaryKey, renderer, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user_id\", \"account_id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void threeColumnPrimaryKey() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("year", "month", "day");

        PsDto result = strategy.handle(primaryKey, renderer, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"year\", \"month\", \"day\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithListConstructor() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition(List.of("column1", "column2"));

        PsDto result = strategy.handle(primaryKey, renderer, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"column1\", \"column2\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithSpecialCharacters() {
        PrimaryKeyDefinition primaryKey = new PrimaryKeyDefinition("user-id", "created_at");

        PsDto result = strategy.handle(primaryKey, renderer, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user-id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }
}
