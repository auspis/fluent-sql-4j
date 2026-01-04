package io.github.massimiliano.fluentsql4j.plugin.builtin.postgre.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.plugin.builtin.postgre.PostgreSqlAstToPreparedStatementSpecVisitorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PostgreSqlDSLTest {

    private PostgreSqlDSL dsl;

    @BeforeEach
    void setUp() {
        dsl = new PostgreSqlDSL(PostgreSqlAstToPreparedStatementSpecVisitorFactory.dialectRendererPostgreSql());
    }

    // STRING_AGG Tests

    @Test
    void stringAggBasic() {
        ScalarExpression expr = dsl.stringAgg("name").build();

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("STRING_AGG");
        assertThat(call.arguments()).hasSize(1);
        assertThat(call.options()).containsEntry("SEPARATOR", ",");
    }

    @Test
    void stringAggWithSeparator() {
        ScalarExpression expr = dsl.stringAgg("name").separator(", ").build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("SEPARATOR", ", ");
    }

    @Test
    void stringAggWithOrderBy() {
        ScalarExpression expr = dsl.stringAgg("name").orderBy("name").build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "name");
    }

    @Test
    void stringAggWithDistinct() {
        ScalarExpression expr = dsl.stringAgg("name").distinct().build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("DISTINCT", true);
    }

    @Test
    void stringAggWithAllOptions() {
        ScalarExpression expr = dsl.stringAgg("email")
                .distinct()
                .orderBy("email DESC")
                .separator(" | ")
                .build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("STRING_AGG");
        assertThat(call.options())
                .containsEntry("DISTINCT", true)
                .containsEntry("ORDER_BY", "email DESC")
                .containsEntry("SEPARATOR", " | ");
    }

    @Test
    void stringAggWithTableReference() {
        ScalarExpression expr = dsl.stringAgg("users", "name").separator(", ").build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        ColumnReference col = (ColumnReference) call.arguments().get(0);
        assertThat(col.table()).isEqualTo("users");
        assertThat(col.column()).isEqualTo("name");
    }

    // ARRAY_AGG Tests

    @Test
    void arrayAggBasic() {
        ScalarExpression expr = dsl.arrayAgg("tags").build();

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("ARRAY_AGG");
        assertThat(call.arguments()).hasSize(1);
    }

    @Test
    void arrayAggWithOrderBy() {
        ScalarExpression expr = dsl.arrayAgg("tags").orderBy("tags").build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "tags");
    }

    @Test
    void arrayAggWithDistinct() {
        ScalarExpression expr = dsl.arrayAgg("category").distinct().build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("DISTINCT", true);
    }

    // JSONB_AGG Tests

    @Test
    void jsonbAggBasic() {
        ScalarExpression expr = dsl.jsonbAgg("data").build();

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("JSONB_AGG");
    }

    @Test
    void jsonbAggWithOrderBy() {
        ScalarExpression expr = dsl.jsonbAgg("item").orderBy("created_at").build();

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.options()).containsEntry("ORDER_BY", "created_at");
    }

    // TO_CHAR Tests

    @Test
    void toCharWithDateFormat() {
        ScalarExpression expr = dsl.toChar(ColumnReference.of("orders", "created_at"), "YYYY-MM-DD");

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("TO_CHAR");
        assertThat(call.arguments()).hasSize(2);
    }

    @Test
    @SuppressWarnings("unchecked")
    void toCharWithTimestampFormat() {
        ScalarExpression expr = dsl.toChar(ColumnReference.of("events", "timestamp"), "YYYY-MM-DD HH24:MI:SS");

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments().get(1)).isInstanceOf(Literal.class);
        Literal<String> format = (Literal<String>) call.arguments().get(1);
        assertThat(format.value()).isEqualTo("YYYY-MM-DD HH24:MI:SS");
    }

    // DATE_TRUNC Tests

    @Test
    void dateTruncDay() {
        ScalarExpression expr = dsl.dateTrunc("day", ColumnReference.of("orders", "created_at"));

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("DATE_TRUNC");
        assertThat(call.arguments()).hasSize(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    void dateTruncMonth() {
        ScalarExpression expr = dsl.dateTrunc("month", ColumnReference.of("sales", "sale_date"));

        CustomFunctionCall call = (CustomFunctionCall) expr;
        Literal<String> field = (Literal<String>) call.arguments().get(0);
        assertThat(field.value()).isEqualTo("month");
    }

    // AGE Tests

    @Test
    void ageWithOneArgument() {
        ScalarExpression expr = dsl.age(ColumnReference.of("users", "birth_date"));

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("AGE");
        assertThat(call.arguments()).hasSize(1);
    }

    @Test
    void ageWithTwoArguments() {
        ScalarExpression expr =
                dsl.age(ColumnReference.of("orders", "completed_at"), ColumnReference.of("orders", "created_at"));

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments()).hasSize(2);
    }

    @Test
    void ageWithNoArgumentsThrowsException() {
        assertThatThrownBy(() -> dsl.age())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AGE requires 1 or 2 arguments");
    }

    @Test
    void ageWithTooManyArgumentsThrowsException() {
        assertThatThrownBy(() -> dsl.age(Literal.of("2023-01-01"), Literal.of("2023-06-01"), Literal.of("2023-12-31")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AGE requires 1 or 2 arguments");
    }

    // COALESCE Tests

    @Test
    void coalesceWithTwoArguments() {
        ScalarExpression expr =
                dsl.coalesce(ColumnReference.of("users", "nickname"), ColumnReference.of("users", "name"));

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("COALESCE");
        assertThat(call.arguments()).hasSize(2);
    }

    @Test
    void coalesceWithMultipleArguments() {
        ScalarExpression expr = dsl.coalesce(
                ColumnReference.of("users", "mobile"), ColumnReference.of("users", "phone"), Literal.of("N/A"));

        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.arguments()).hasSize(3);
    }

    @Test
    void coalesceWithOneArgumentThrowsException() {
        assertThatThrownBy(() -> dsl.coalesce(Literal.of("value")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("COALESCE requires at least 2 arguments");
    }

    // NULLIF Tests

    @Test
    void nullIfBasic() {
        ScalarExpression expr = dsl.nullIf(ColumnReference.of("products", "status"), Literal.of("deleted"));

        assertThat(expr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) expr;
        assertThat(call.functionName()).isEqualTo("NULLIF");
        assertThat(call.arguments()).hasSize(2);
    }
}
