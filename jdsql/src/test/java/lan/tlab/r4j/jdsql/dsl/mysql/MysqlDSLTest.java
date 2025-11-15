package lan.tlab.r4j.jdsql.dsl.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.MysqlDSL;
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

        Object result = dsl.groupConcat("name", ", ");

        assertThat(result).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) result;
        assertThat(call.functionName()).isEqualTo("GROUP_CONCAT");
        assertThat(call.arguments()).hasSize(1);
        assertThat(call.options()).containsEntry("SEPARATOR", ", ");
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
    void shouldProvideIfExprFluentAPI() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var selectBuilder = dsl.select();
        assertThat(selectBuilder).isNotNull();

        var ifBuilder = selectBuilder.ifExpr();
        assertThat(ifBuilder).isNotNull();

        var conditionBuilder = ifBuilder.when("age");
        assertThat(conditionBuilder).isNotNull();

        var thenBuilder = conditionBuilder.gte(18);
        assertThat(thenBuilder).isNotNull();

        var finalBuilder = thenBuilder.then("adult");
        assertThat(finalBuilder).isNotNull();

        var aliasBuilder = finalBuilder.otherwise("minor");
        assertThat(aliasBuilder).isNotNull();
    }

    @Test
    void shouldProvideDateFormatFluentAPI() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var selectBuilder = dsl.select();
        assertThat(selectBuilder).isNotNull();

        var resultBuilder = selectBuilder.dateFormat("birthdate", "%Y-%m-%d");
        assertThat(resultBuilder).isNotNull();
        assertThat(resultBuilder).isSameAs(selectBuilder);
    }

    @Test
    void ifExprShouldRejectNullColumnInWhen() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().ifExpr().when(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ifExprShouldRejectNullValueInThen() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().ifExpr().when("age").gte(18).then(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ifExprShouldRejectNullValueInOtherwise() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() ->
                        dsl.select().ifExpr().when("age").gte(18).then("adult").otherwise(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void dateFormatShouldRejectNullColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().dateFormat(null, "%Y-%m-%d")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void dateFormatShouldRejectNullFormat() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().dateFormat("birthdate", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldProvideInheritedAggregateFunctions() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder = dsl.select().sum("amount").countDistinct("user_id");

        assertThat(builder).isNotNull();
    }

    @Test
    void shouldProvideConcatBuilderFluentAPI() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var concatBuilder = dsl.select().concat();

        assertThat(concatBuilder).isNotNull();
    }

    @Test
    void concatBuilderShouldBuildWithMultipleColumns() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder =
                dsl.select().concat().column("first_name").column("last_name").as("full_name");

        assertThat(builder).isNotNull();
    }

    @Test
    void concatBuilderShouldSupportTableQualifiedColumns() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder = dsl.select()
                .concat()
                .column("users", "first_name")
                .literal(" ")
                .column("users", "last_name")
                .as("full_name");

        assertThat(builder).isNotNull();
    }

    @Test
    void concatBuilderShouldRejectNullColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().concat().column((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void concatBuilderShouldRejectEmptyExpressionList() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().concat().as("result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CONCAT requires at least one expression");
    }

    @Test
    void shouldProvideCoalesceBuilderFluentAPI() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var coalesceBuilder = dsl.select().coalesce();

        assertThat(coalesceBuilder).isNotNull();
    }

    @Test
    void coalesceBuilderShouldBuildWithMultipleColumns() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder = dsl.select().coalesce().column("mobile").column("phone").as("contact");

        assertThat(builder).isNotNull();
    }

    @Test
    void coalesceBuilderShouldSupportTableQualifiedColumns() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder = dsl.select()
                .coalesce()
                .column("users", "mobile")
                .column("users", "phone")
                .literal("no-phone")
                .as("contact");

        assertThat(builder).isNotNull();
    }

    @Test
    void coalesceBuilderShouldRejectNullColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().coalesce().column((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void coalesceBuilderShouldRequireAtLeastTwoExpressions() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().coalesce().column("column1").as("result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("COALESCE requires at least two expressions");
    }

    @Test
    void shouldProvideIfnullFluentAPI() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        var builder = dsl.select().ifnull("email", "no-email");

        assertThat(builder).isNotNull();
    }

    @Test
    void ifnullShouldRejectNullColumn() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().ifnull(null, "default"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void ifnullShouldRejectNullDefaultValue() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThatThrownBy(() -> dsl.select().ifnull("email", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Default value must not be null");
    }

    @Test
    void shouldInheritAllBaseDSLMethods() {
        DialectRenderer renderer = mock(DialectRenderer.class);
        MysqlDSL dsl = new MysqlDSL(renderer);

        assertThat(dsl.select("name")).isNotNull();
        assertThat(dsl.selectAll()).isNotNull();
        assertThat(dsl.insertInto("users")).isNotNull();
        assertThat(dsl.deleteFrom("users")).isNotNull();
        assertThat(dsl.update("users")).isNotNull();
        assertThat(dsl.mergeInto("users")).isNotNull();
        assertThat(dsl.createTable("users")).isNotNull();
    }
}
