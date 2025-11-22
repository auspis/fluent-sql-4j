package lan.tlab.r4j.jdsql.ast.common.expression.scalar.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import org.junit.jupiter.api.Test;

class CustomFunctionCallTest {

    @Test
    void createsWithValidParameters() {
        CustomFunctionCall call =
                new CustomFunctionCall("GROUP_CONCAT", List.of(ColumnReference.of("t", "col")), Map.of());

        assertThat(call.functionName()).isEqualTo("GROUP_CONCAT");
        assertThat(call.arguments()).hasSize(1);
        assertThat(call.options()).isEmpty();
    }

    @Test
    void createsWithNullArgumentsAndOptions() {
        CustomFunctionCall call = new CustomFunctionCall("RAND", null, null);

        assertThat(call.functionName()).isEqualTo("RAND");
        assertThat(call.arguments()).isEmpty();
        assertThat(call.options()).isEmpty();
    }

    @Test
    void createsWithMultipleArguments() {
        ScalarExpression arg1 = ColumnReference.of("t", "col1");
        ScalarExpression arg2 = Literal.of("separator");
        ScalarExpression arg3 = ColumnReference.of("t", "col2");

        CustomFunctionCall call = new CustomFunctionCall("CONCAT_WS", List.of(arg1, arg2, arg3), Map.of());

        assertThat(call.arguments()).hasSize(3);
        assertThat(call.arguments().get(0)).isEqualTo(arg1);
        assertThat(call.arguments().get(1)).isEqualTo(arg2);
        assertThat(call.arguments().get(2)).isEqualTo(arg3);
    }

    @Test
    void createsWithOptions() {
        Map<String, Object> options = Map.of("ORDER_BY", "id", "SEPARATOR", ",", "DISTINCT", true);

        CustomFunctionCall call =
                new CustomFunctionCall("GROUP_CONCAT", List.of(ColumnReference.of("t", "name")), options);

        assertThat(call.options()).hasSize(3);
        assertThat(call.options().get("ORDER_BY")).isEqualTo("id");
        assertThat(call.options().get("SEPARATOR")).isEqualTo(",");
        assertThat(call.options().get("DISTINCT")).isEqualTo(true);
    }

    @Test
    void throwsExceptionWhenFunctionNameIsNull() {
        assertThatThrownBy(() -> new CustomFunctionCall(null, List.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Function name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenFunctionNameIsEmpty() {
        assertThatThrownBy(() -> new CustomFunctionCall("", List.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Function name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenFunctionNameIsBlank() {
        assertThatThrownBy(() -> new CustomFunctionCall("   ", List.of(), Map.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Function name cannot be null or empty");
    }

    @Test
    void argumentsListIsImmutable() {
        List<ScalarExpression> args = List.of(ColumnReference.of("t", "col"));
        CustomFunctionCall call = new CustomFunctionCall("TEST_FUNC", args, Map.of());

        assertThatThrownBy(() -> call.arguments().add(Literal.of("test")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void optionsMapIsImmutable() {
        Map<String, Object> options = Map.of("KEY", "value");
        CustomFunctionCall call = new CustomFunctionCall("TEST_FUNC", List.of(), options);

        assertThatThrownBy(() -> call.options().put("NEW_KEY", "new_value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void preservesArgumentsOrder() {
        ScalarExpression arg1 = Literal.of(1);
        ScalarExpression arg2 = Literal.of(2);
        ScalarExpression arg3 = Literal.of(3);

        CustomFunctionCall call = new CustomFunctionCall("TEST_FUNC", List.of(arg1, arg2, arg3), Map.of());

        assertThat(call.arguments()).containsExactly(arg1, arg2, arg3);
    }

    @Test
    void handlesEmptyArguments() {
        CustomFunctionCall call = new CustomFunctionCall("RAND", List.of(), Map.of());

        assertThat(call.arguments()).isEmpty();
    }

    @Test
    void handlesEmptyOptions() {
        CustomFunctionCall call = new CustomFunctionCall("MD5", List.of(ColumnReference.of("t", "password")), Map.of());

        assertThat(call.options()).isEmpty();
    }

    @Test
    void differentInstancesWithSameValuesAreEqual() {
        ScalarExpression arg = ColumnReference.of("t", "col");
        Map<String, Object> options = Map.of("KEY", "value");

        CustomFunctionCall call1 = new CustomFunctionCall("FUNC", List.of(arg), options);
        CustomFunctionCall call2 = new CustomFunctionCall("FUNC", List.of(arg), options);

        assertThat(call1).isEqualTo(call2);
        assertThat(call1.hashCode()).isEqualTo(call2.hashCode());
    }

    @Test
    void differentInstancesWithDifferentValuesAreNotEqual() {
        CustomFunctionCall call1 = new CustomFunctionCall("FUNC1", List.of(), Map.of());
        CustomFunctionCall call2 = new CustomFunctionCall("FUNC2", List.of(), Map.of());

        assertThat(call1).isNotEqualTo(call2);
    }

    @Test
    void toStringContainsFunctionName() {
        CustomFunctionCall call = new CustomFunctionCall("GROUP_CONCAT", List.of(), Map.of());

        assertThat(call.toString()).contains("GROUP_CONCAT");
    }
}
