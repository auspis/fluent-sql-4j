package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.PrimaryKeyPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultPrimaryKeyPsStrategyTest {

    private final PrimaryKeyPsStrategy strategy = new DefaultPrimaryKeyPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void singleColumnPrimaryKey() {
        PrimaryKey primaryKey = new PrimaryKey("id");

        PsDto result = strategy.handle(primaryKey, visitor, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multiColumnPrimaryKey() {
        PrimaryKey primaryKey = new PrimaryKey("user_id", "account_id");

        PsDto result = strategy.handle(primaryKey, visitor, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user_id\", \"account_id\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void threeColumnPrimaryKey() {
        PrimaryKey primaryKey = new PrimaryKey("year", "month", "day");

        PsDto result = strategy.handle(primaryKey, visitor, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"year\", \"month\", \"day\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithListConstructor() {
        PrimaryKey primaryKey = new PrimaryKey(List.of("column1", "column2"));

        PsDto result = strategy.handle(primaryKey, visitor, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"column1\", \"column2\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void primaryKeyWithSpecialCharacters() {
        PrimaryKey primaryKey = new PrimaryKey("user-id", "created_at");

        PsDto result = strategy.handle(primaryKey, visitor, ctx);

        assertThat(result.sql()).isEqualTo("PRIMARY KEY (\"user-id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }
}
