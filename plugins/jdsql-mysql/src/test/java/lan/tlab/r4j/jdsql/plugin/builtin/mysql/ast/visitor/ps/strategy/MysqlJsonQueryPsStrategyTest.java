package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.BehaviorKind;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.JsonQuery;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.OnEmptyBehavior;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.json.WrapperBehavior;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.escape.MysqlEscapeStrategy;
import org.junit.jupiter.api.Test;

class MysqlJsonQueryPsStrategyTest {

    @Test
    void withBasicArguments() {
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("users", "address"), Literal.of("$"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`address`, ?)");
        assertThat(result.parameters()).containsExactly("$");
    }

    @Test
    void withTableQualifiedColumn() {
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.items"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // PreparedStatementRenderer outputs only column name when no table context
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`data`, ?)");
        assertThat(result.parameters()).containsExactly("$.items");
    }

    @Test
    void withReturningTypeIsIgnored() {
        // MySQL does not support RETURNING - it should be ignored
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("orders", "items"),
                Literal.of("$.products"),
                "JSON",
                WrapperBehavior.NONE,
                OnEmptyBehavior.returnNull(),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // RETURNING type should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`items`, ?)");
        assertThat(result.parameters()).containsExactly("$.products");
    }

    @Test
    void withWrapperBehaviorIsIgnored() {
        // MySQL does not support wrapper behavior - it should be ignored
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("users", "preferences"),
                Literal.of("$.tags"),
                null,
                WrapperBehavior.WITH_WRAPPER,
                OnEmptyBehavior.returnNull(),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // Wrapper behavior should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`preferences`, ?)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }

    @Test
    void withAllOptionsIgnored() {
        // MySQL does not support RETURNING, wrapper, ON EMPTY, or ON ERROR
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("orders", "data"),
                Literal.of("$.history"),
                "JSON",
                WrapperBehavior.WITHOUT_WRAPPER,
                OnEmptyBehavior.defaultValue("{}"),
                BehaviorKind.ERROR);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        // All SQL:2016 options should be ignored
        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`data`, ?)");
        assertThat(result.parameters()).containsExactly("$.history");
    }

    @Test
    void withArrayPath() {
        PreparedStatementRenderer specFactory = PreparedStatementRenderer.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MysqlJsonQueryPsStrategy strategy = new MysqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("documents", "metadata"), Literal.of("$.tags[*]"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_EXTRACT(`metadata`, ?)");
        assertThat(result.parameters()).containsExactly("$.tags[*]");
    }
}
