package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.IndexDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlIndexPsStrategyTest {

    private final IndexDefinitionPsStrategy strategy = new StandardSqlIndexDefinitionPsStrategy();
    private final PreparedStatementRenderer specFactory = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void singleColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_name", "name");

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_name\" (\"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multiColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_user_created", "user_id", "created_at");

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_user_created\" (\"user_id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void threeColumnIndex() {
        IndexDefinition index = new IndexDefinition("idx_composite", "year", "month", "day");

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_composite\" (\"year\", \"month\", \"day\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithListConstructor() {
        IndexDefinition index = new IndexDefinition("idx_columns", List.of("column1", "column2"));

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_columns\" (\"column1\", \"column2\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithSpecialCharacters() {
        IndexDefinition index = new IndexDefinition("idx-special_name", "user-id", "created_at");

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx-special_name\" (\"user-id\", \"created_at\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void indexWithUnderscoreNaming() {
        IndexDefinition index = new IndexDefinition("idx_table_field", "table_field");

        PreparedStatementSpec result = strategy.handle(index, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("INDEX \"idx_table_field\" (\"table_field\")");
        assertThat(result.parameters()).isEmpty();
    }
}
