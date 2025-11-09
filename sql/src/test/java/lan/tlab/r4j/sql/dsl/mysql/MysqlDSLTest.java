package lan.tlab.r4j.sql.dsl.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import org.junit.jupiter.api.Test;

class MysqlDSLTest {

    @Test
    void shouldExtendBaseDSL() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThat(dsl).isInstanceOf(DSL.class);
        assertThat(dsl.getRenderer()).isEqualTo(renderer);
    }

    @Test
    void shouldProvideGroupConcatMethod() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Currently throws UnsupportedOperationException - will be implemented in Task 8
        assertThatThrownBy(() -> dsl.groupConcat("name", ", "))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("groupConcat() will be implemented in Task 8");
    }

    @Test
    void shouldProvideIfExprMethod() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Currently throws UnsupportedOperationException - will be implemented in Task 8
        assertThatThrownBy(() -> dsl.ifExpr("age > 18", "'adult'", "'minor'"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("ifExpr() will be implemented in Task 8");
    }

    @Test
    void shouldProvideDateFormatMethod() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Currently throws UnsupportedOperationException - will be implemented in Task 8
        assertThatThrownBy(() -> dsl.dateFormat("birth_date", "%Y-%m-%d"))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("dateFormat() will be implemented in Task 8");
    }

    @Test
    void shouldProvideNowMethod() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Currently throws UnsupportedOperationException - will be implemented in Task 8
        assertThatThrownBy(() -> dsl.now())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("now() will be implemented in Task 8");
    }

    @Test
    void shouldProvideCurDateMethod() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Currently throws UnsupportedOperationException - will be implemented in Task 8
        assertThatThrownBy(() -> dsl.curDate())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("curDate() will be implemented in Task 8");
    }

    @Test
    void groupConcatShouldRejectNullColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.groupConcat(null, ", "))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void groupConcatShouldRejectNullSeparator() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.groupConcat("name", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Separator must not be null");
    }

    @Test
    void ifExprShouldRejectNullCondition() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.ifExpr(null, "true", "false"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Condition must not be null");
    }

    @Test
    void ifExprShouldRejectNullTrueValue() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.ifExpr("condition", null, "false"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("True value must not be null");
    }

    @Test
    void ifExprShouldRejectNullFalseValue() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.ifExpr("condition", "true", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("False value must not be null");
    }

    @Test
    void dateFormatShouldRejectNullDateColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.dateFormat(null, "%Y-%m-%d"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Date column must not be null");
    }

    @Test
    void dateFormatShouldRejectNullFormat() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.dateFormat("birth_date", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Format must not be null");
    }

    @Test
    void shouldInheritAllBaseDSLMethods() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        // Verify that MySQLDSL can use base DSL methods
        assertThat(dsl.select("name")).isNotNull();
        assertThat(dsl.selectAll()).isNotNull();
        assertThat(dsl.insertInto("users")).isNotNull();
        assertThat(dsl.deleteFrom("users")).isNotNull();
        assertThat(dsl.update("users")).isNotNull();
        assertThat(dsl.mergeInto("users")).isNotNull();
        assertThat(dsl.createTable("users")).isNotNull();
    }
}
