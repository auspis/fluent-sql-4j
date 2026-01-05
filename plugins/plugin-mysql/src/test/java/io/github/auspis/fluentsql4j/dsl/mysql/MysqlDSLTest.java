package io.github.auspis.fluentsql4j.dsl.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import org.junit.jupiter.api.Test;

class MysqlDSLTest {

    @Test
    void shouldExtendBaseDSL() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThat(dsl).isInstanceOf(DSL.class);
        assertThat(dsl.getSpecFactory()).isEqualTo(specFactory);
    }

    @Test
    void shouldProvideGroupConcatMethod() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        Object result = dsl.groupConcat("name", ", ");

        assertThat(result).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) result;
        assertThat(call.functionName()).isEqualTo("GROUP_CONCAT");
        assertThat(call.arguments()).hasSize(1);
        assertThat(call.options()).containsEntry("SEPARATOR", ", ");
    }

    @Test
    void groupConcatShouldRejectNullColumn() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.groupConcat(null, ", "))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void groupConcatShouldRejectNullSeparator() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.groupConcat("name", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Separator must not be null");
    }

    @Test
    void shouldProvideIfExprFluentAPI() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

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
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var selectBuilder = dsl.select();
        assertThat(selectBuilder).isNotNull();

        var resultBuilder = selectBuilder.dateFormat("birthdate", "%Y-%m-%d");
        assertThat(resultBuilder).isNotNull();
        assertThat(resultBuilder).isSameAs(selectBuilder);
    }

    @Test
    void ifExprShouldRejectNullColumnInWhen() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().ifExpr().when(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void ifExprShouldRejectNullValueInThen() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().ifExpr().when("age").gte(18).then(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void ifExprShouldRejectNullValueInOtherwise() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() ->
                        dsl.select().ifExpr().when("age").gte(18).then("adult").otherwise(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void dateFormatShouldRejectNullColumn() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().dateFormat(null, "%Y-%m-%d")).isInstanceOf(NullPointerException.class);
    }

    @Test
    void dateFormatShouldRejectNullFormat() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().dateFormat("birthdate", null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldProvideInheritedAggregateFunctions() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var builder = dsl.select().sum("amount").countDistinct("user_id");

        assertThat(builder).isNotNull();
    }

    @Test
    void shouldProvideConcatBuilderFluentAPI() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var concatBuilder = dsl.select().concat();

        assertThat(concatBuilder).isNotNull();
    }

    @Test
    void concatBuilderShouldBuildWithMultipleColumns() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var builder =
                dsl.select().concat().column("first_name").column("last_name").as("full_name");

        assertThat(builder).isNotNull();
    }

    @Test
    void concatBuilderShouldSupportTableQualifiedColumns() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

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
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().concat().column((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void concatBuilderShouldRejectEmptyExpressionList() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().concat().as("result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CONCAT requires at least one expression");
    }

    @Test
    void shouldProvideCoalesceBuilderFluentAPI() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var coalesceBuilder = dsl.select().coalesce();

        assertThat(coalesceBuilder).isNotNull();
    }

    @Test
    void coalesceBuilderShouldBuildWithMultipleColumns() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var builder = dsl.select().coalesce().column("mobile").column("phone").as("contact");

        assertThat(builder).isNotNull();
    }

    @Test
    void coalesceBuilderShouldSupportTableQualifiedColumns() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

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
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().coalesce().column((String) null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void coalesceBuilderShouldRequireAtLeastTwoExpressions() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().coalesce().column("column1").as("result"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("COALESCE requires at least two expressions");
    }

    @Test
    void shouldProvideIfnullFluentAPI() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        var builder = dsl.select().ifnull("email", "no-email");

        assertThat(builder).isNotNull();
    }

    @Test
    void ifnullShouldRejectNullColumn() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().ifnull(null, "default"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Column must not be null");
    }

    @Test
    void ifnullShouldRejectNullDefaultValue() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThatThrownBy(() -> dsl.select().ifnull("email", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Default value must not be null");
    }

    @Test
    void shouldInheritAllBaseDSLMethods() {
        PreparedStatementSpecFactory specFactory = mock(PreparedStatementSpecFactory.class);
        MysqlDSL dsl = new MysqlDSL(specFactory);

        assertThat(dsl.select("name")).isNotNull();
        assertThat(dsl.selectAll()).isNotNull();
        assertThat(dsl.insertInto("users")).isNotNull();
        assertThat(dsl.deleteFrom("users")).isNotNull();
        assertThat(dsl.update("users")).isNotNull();
        assertThat(dsl.mergeInto("users")).isNotNull();
        assertThat(dsl.createTable("users")).isNotNull();
    }
}
