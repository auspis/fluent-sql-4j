package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MysqlStringLiteralEscapeStrategyTest {

    @Test
    void escapesSingleQuotes() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape("it's a test");
        assertThat(result).isEqualTo("it''s a test");
    }

    @Test
    void escapesMultipleSingleQuotes() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape("it's John's book");
        assertThat(result).isEqualTo("it''s John''s book");
    }

    @Test
    void preventsSqlInjection() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape("', DROP TABLE users--");
        assertThat(result).isEqualTo("'', DROP TABLE users--");
    }

    @Test
    void handlesEmptyString() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape("");
        assertThat(result).isEmpty();
    }

    @Test
    void handlesStringWithNoQuotes() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape("Hello World");
        assertThat(result).isEqualTo("Hello World");
    }

    @Test
    void handlesNullInput() {
        MysqlStringLiteralEscapeStrategy strategy = new MysqlStringLiteralEscapeStrategy();
        String result = strategy.escape(null);
        assertThat(result).isEmpty();
    }
}
