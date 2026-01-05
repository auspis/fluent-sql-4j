package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.ddl.definition.IndexDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IndexDefinitionPsStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlIndexPsStrategyTest {

    private final IndexDefinitionPsStrategy strategy = new StandardSqlIndexDefinitionPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
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
