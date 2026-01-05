package io.github.auspis.fluentsql4j.functional;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.functional.Result;

class ResultPeekAndOrElseGetTest {

    @Test
    void peek_onSuccess_shouldExecuteConsumer() {
        List<String> sideEffects = new ArrayList<>();
        Result<String> result = new Result.Success<>("test value");

        Result<String> peeked = result.peek(value -> sideEffects.add("Saw: " + value));

        assertThat(peeked).isInstanceOf(Result.Success.class);
        assertThat(peeked.orElseThrow()).isEqualTo("test value");
        assertThat(sideEffects).containsExactly("Saw: test value");
    }

    @Test
    void peek_onFailure_shouldNotExecuteConsumer() {
        List<String> sideEffects = new ArrayList<>();
        Result<String> result = new Result.Failure<>("error occurred");

        Result<String> peeked = result.peek(value -> sideEffects.add("Saw: " + value));

        assertThat(peeked).isInstanceOf(Result.Failure.class);
        assertThat(sideEffects).isEmpty();
    }

    @Test
    void peek_shouldReturnSameResultInstance() {
        Result<String> result = new Result.Success<>("test");

        Result<String> peeked = result.peek(value -> {
            // Side effect
        });

        assertThat(peeked).isSameAs(result);
    }

    @Test
    void peek_canBeChained() {
        List<String> steps = new ArrayList<>();
        Result<Integer> result = new Result.Success<>(42);

        String finalValue = result.peek(v -> steps.add("step1: " + v))
                .map(v -> v * 2)
                .peek(v -> steps.add("step2: " + v))
                .map(String::valueOf)
                .peek(v -> steps.add("step3: " + v))
                .orElseThrow();

        assertThat(finalValue).isEqualTo("84");
        assertThat(steps).containsExactly("step1: 42", "step2: 84", "step3: 84");
    }

    @Test
    void orElseGet_onSuccess_shouldReturnValue() {
        Result<String> result = new Result.Success<>("original value");

        String value = result.orElseGet(() -> "default value");

        assertThat(value).isEqualTo("original value");
    }

    @Test
    void orElseGet_onFailure_shouldReturnSuppliedValue() {
        Result<String> result = new Result.Failure<>("error occurred");

        String value = result.orElseGet(() -> "default value");

        assertThat(value).isEqualTo("default value");
    }

    @Test
    void orElseGet_shouldBeLazy() {
        AtomicInteger callCount = new AtomicInteger(0);
        Result<String> result = new Result.Success<>("original");

        String value = result.orElseGet(() -> {
            callCount.incrementAndGet();
            return "default";
        });

        assertThat(value).isEqualTo("original");
        assertThat(callCount.get()).isZero(); // Supplier not called on Success
    }

    @Test
    void orElseGet_withExpensiveOperation() {
        Result<String> result = new Result.Failure<>("error");
        AtomicInteger callCount = new AtomicInteger(0);

        String value = result.orElseGet(() -> {
            callCount.incrementAndGet();
            return "computed default";
        });

        assertThat(value).isEqualTo("computed default");
        assertThat(callCount.get()).isEqualTo(1); // Supplier called on Failure
    }

    @Test
    void orElseGet_vsOrElse_performanceDifference() {
        Result<String> successResult = new Result.Success<>("value");
        AtomicInteger expensiveCallCount = new AtomicInteger(0);

        // orElse evaluates eagerly
        String value1 = successResult.orElse(expensiveComputation(expensiveCallCount));
        assertThat(expensiveCallCount.get()).isEqualTo(1); // Called even for Success

        expensiveCallCount.set(0);

        // orElseGet evaluates lazily
        String value2 = successResult.orElseGet(() -> expensiveComputation(expensiveCallCount));
        assertThat(expensiveCallCount.get()).isZero(); // Not called for Success

        assertThat(value1).isEqualTo(value2).isEqualTo("value");
    }

    private String expensiveComputation(AtomicInteger counter) {
        counter.incrementAndGet();
        return "expensive result";
    }
}
