package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultIndexPsStrategyTest {

    private final IndexDefinitionPsStrategy strategy = new DefaultIndexPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void singleColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_name", "name");

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_name\" (\"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multiColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_user_created", "user_id", "created_at");

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_user_created\" (\"user_id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void threeColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_composite", "year", "month", "day");

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_composite\" (\"year\", \"month\", \"day\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithListConstructor() {
        IndexDefinition index = new IndexDefinition("idx_columns", List.of("column1", "column2"));

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_columns\" (\"column1\", \"column2\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithSpecialCharacters() {
        IndexDefinition index = new IndexDefinition("idx-special_name", "user-id", "created_at");

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx-special_name\" (\"user-id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithUnderscoreNaming() {
        IndexDefinition index = new IndexDefinition("idx_table_field", "table_field");

        PsDto result = strategy.handle(index, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_table_field\" (\"table_field\")");
        assertThat(result.parameters()).isEmpty();
    }
}
