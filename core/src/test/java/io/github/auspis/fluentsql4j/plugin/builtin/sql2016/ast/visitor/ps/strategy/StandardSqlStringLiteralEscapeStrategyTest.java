package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StandardSqlStringLiteralEscapeStrategyTest {

    @Test
    void escapesSingleQuotes() {
        StandardSqlStringLiteralEscapeStrategy strategy = new StandardSqlStringLiteralEscapeStrategy();
        String result = strategy.escape("it's a test");
        assertThat(result).isEqualTo("it''s a test");
    }

    @Test
    void escapesMultipleSingleQuotes() {
        StandardSqlStringLiteralEscapeStrategy strategy = new StandardSqlStringLiteralEscapeStrategy();
        String result = strategy.escape("it's John's book");
        assertThat(result).isEqualTo("it''s John''s book");
    }

    @Test
    void preventsSqlInjection() {
        StandardSqlStringLiteralEscapeStrategy strategy = new StandardSqlStringLiteralEscapeStrategy();
        String result = strategy.escape("', DROP TABLE users--");
        assertThat(result).isEqualTo("'', DROP TABLE users--");
    }

    @Test
    void handlesEmptyString() {
        StandardSqlStringLiteralEscapeStrategy strategy = new StandardSqlStringLiteralEscapeStrategy();
        String result = strategy.escape("");
        assertThat(result).isEmpty();
    }

    @Test
    void handlesStringWithNoQuotes() {
        StandardSqlStringLiteralEscapeStrategy strategy = new StandardSqlStringLiteralEscapeStrategy();
        String result = strategy.escape("Hello World");
        assertThat(result).isEqualTo("Hello World");
    }
}
