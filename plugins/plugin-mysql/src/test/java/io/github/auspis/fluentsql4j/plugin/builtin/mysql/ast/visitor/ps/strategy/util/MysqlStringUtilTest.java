package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MysqlStringUtilTest {

    @Test
    void escapesSingleQuote() {
        String result = MysqlStringUtil.escape("it's");
        assertThat(result).isEqualTo("it''s");
    }

    @Test
    void escapesMultipleSingleQuotes() {
        String result = MysqlStringUtil.escape("don't do it'");
        assertThat(result).isEqualTo("don''t do it''");
    }

    @Test
    void leavesStringUnchangedWhenNoQuotes() {
        String result = MysqlStringUtil.escape("hello world");
        assertThat(result).isEqualTo("hello world");
    }

    @Test
    void handlesEmptyString() {
        String result = MysqlStringUtil.escape("");
        assertThat(result).isEqualTo("");
    }

    @Test
    void escapesOnlySingleQuotesLeavingOtherChars() {
        String result = MysqlStringUtil.escape("a'b\"c\\d");
        assertThat(result).isEqualTo("a''b\"c\\d");
    }

    @Test
    void throwsNullPointerExceptionForNullInput() {
        assertThatThrownBy(() -> MysqlStringUtil.escape(null)).isInstanceOf(NullPointerException.class);
    }
}
