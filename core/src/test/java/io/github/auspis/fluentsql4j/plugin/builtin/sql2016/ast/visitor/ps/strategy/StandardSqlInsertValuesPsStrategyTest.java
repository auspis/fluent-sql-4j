package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.Expression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData.InsertValues;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlInsertValuesPsStrategy;

class StandardSqlInsertValuesPsStrategyTest {

    private StandardSqlInsertValuesPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlInsertValuesPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void emptyValues() {
        List<Expression> values = List.of();
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleValue() {
        List<Expression> values = List.of(Literal.of("John"));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void multipleValues() {
        List<Expression> values = List.of(Literal.of(1), Literal.of("Alice"), Literal.of("alice@example.com"));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly(1, "Alice", "alice@example.com");
    }

    @Test
    void mixedTypes() {
        List<Expression> values =
                List.of(Literal.of(42), Literal.of("Widget"), Literal.of(99.99), Literal.of(true), Literal.of(false));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?, ?, ?");
        assertThat(result.parameters()).containsExactly(42, "Widget", 99.99, true, false);
    }

    @Test
    void stringValues() {
        List<Expression> values = List.of(Literal.of("Hello"), Literal.of("World"), Literal.of("Test"));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly("Hello", "World", "Test");
    }

    @Test
    void numericValues() {
        List<Expression> values = List.of(Literal.of(1), Literal.of(2L), Literal.of(3.14), Literal.of(4.56f));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?, ?");
        assertThat(result.parameters()).containsExactly(1, 2L, 3.14, 4.56f);
    }

    @Test
    void booleanValues() {
        List<Expression> values = List.of(Literal.of(true), Literal.of(false), Literal.of(true));
        InsertValues insertValues = new InsertValues(values);

        PreparedStatementSpec result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly(true, false, true);
    }
}
