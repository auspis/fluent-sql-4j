package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.function.json.BehaviorKind;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.JsonQuery;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.OnEmptyBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.function.json.WrapperBehavior;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.JsonQueryPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlJsonQueryPsStrategyTest {

    @Test
    void withBasicArguments() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        JsonQueryPsStrategy strategy = new StandardSqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(ColumnReference.of("products", "data"), Literal.of("$.tags"));

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("JSON_QUERY(\"data\", ?)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }

    @Test
    void withAllOptions() {
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        JsonQueryPsStrategy strategy = new StandardSqlJsonQueryPsStrategy();
        JsonQuery jsonQuery = new JsonQuery(
                ColumnReference.of("products", "data"),
                Literal.of("$.tags"),
                "JSON",
                WrapperBehavior.WITH_WRAPPER,
                OnEmptyBehavior.defaultValue("EMPTY ARRAY"),
                BehaviorKind.NONE);

        PreparedStatementSpec result = strategy.handle(jsonQuery, specFactory, new AstContext());

        assertThat(result.sql())
                .isEqualTo("JSON_QUERY(\"data\", ? RETURNING JSON WITH WRAPPER DEFAULT EMPTY ARRAY ON EMPTY)");
        assertThat(result.parameters()).containsExactly("$.tags");
    }
}
