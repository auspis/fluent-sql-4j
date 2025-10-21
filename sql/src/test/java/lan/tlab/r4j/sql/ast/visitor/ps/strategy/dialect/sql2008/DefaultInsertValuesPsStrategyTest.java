package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.InsertValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultInsertValuesPsStrategyTest {

    private DefaultInsertValuesPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultInsertValuesPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void emptyValues() {
        List<Expression> values = List.of();
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void singleValue() {
        List<Expression> values = List.of(Literal.of("John"));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void multipleValues() {
        List<Expression> values = List.of(Literal.of(1), Literal.of("Alice"), Literal.of("alice@example.com"));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly(1, "Alice", "alice@example.com");
    }

    @Test
    void mixedTypes() {
        List<Expression> values =
                List.of(Literal.of(42), Literal.of("Widget"), Literal.of(99.99), Literal.of(true), Literal.of(false));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?, ?, ?");
        assertThat(result.parameters()).containsExactly(42, "Widget", 99.99, true, false);
    }

    @Test
    void stringValues() {
        List<Expression> values = List.of(Literal.of("Hello"), Literal.of("World"), Literal.of("Test"));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly("Hello", "World", "Test");
    }

    @Test
    void numericValues() {
        List<Expression> values = List.of(Literal.of(1), Literal.of(2L), Literal.of(3.14), Literal.of(4.56f));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?, ?");
        assertThat(result.parameters()).containsExactly(1, 2L, 3.14, 4.56f);
    }

    @Test
    void booleanValues() {
        List<Expression> values = List.of(Literal.of(true), Literal.of(false), Literal.of(true));
        InsertValues insertValues = new InsertValues(values);

        PsDto result = strategy.handle(insertValues, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?, ?, ?");
        assertThat(result.parameters()).containsExactly(true, false, true);
    }
}
