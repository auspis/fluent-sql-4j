package io.github.auspis.fluentsql4j.dsl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class ColumnReferenceUtilTest {

    @Test
    void retargetColumnWithNoTable() {
        ColumnReference colRef = ColumnReference.of("", "name");
        ColumnReference result = ColumnReferenceUtil.retargetIfApplicable(colRef, "users", "u");

        assertThat(result.table()).isEqualTo("u");
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void retargetColumnWithNullTable() {
        ColumnReference colRef = ColumnReference.of(null, "name");
        ColumnReference result = ColumnReferenceUtil.retargetIfApplicable(colRef, "users", "u");

        assertThat(result.table()).isEqualTo("u");
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void retargetColumnWithMatchingTable() {
        ColumnReference colRef = ColumnReference.of("users", "name");
        ColumnReference result = ColumnReferenceUtil.retargetIfApplicable(colRef, "users", "u");

        assertThat(result.table()).isEqualTo("u");
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void doNotRetargetColumnWithDifferentTable() {
        ColumnReference colRef = ColumnReference.of("orders", "id");
        ColumnReference result = ColumnReferenceUtil.retargetIfApplicable(colRef, "users", "u");

        assertThat(result.table()).isEqualTo("orders");
        assertThat(result.column()).isEqualTo("id");
    }

    @Test
    void shouldRetargetWhenNoTable() {
        ColumnReference colRef = ColumnReference.of("", "name");
        assertThat(ColumnReferenceUtil.shouldRetarget(colRef, "users")).isTrue();
    }

    @Test
    void shouldRetargetWhenNullTable() {
        ColumnReference colRef = ColumnReference.of(null, "name");
        assertThat(ColumnReferenceUtil.shouldRetarget(colRef, "users")).isTrue();
    }

    @Test
    void shouldRetargetWhenMatchingTable() {
        ColumnReference colRef = ColumnReference.of("users", "name");
        assertThat(ColumnReferenceUtil.shouldRetarget(colRef, "users")).isTrue();
    }

    @Test
    void shouldNotRetargetWhenDifferentTable() {
        ColumnReference colRef = ColumnReference.of("orders", "id");
        assertThat(ColumnReferenceUtil.shouldRetarget(colRef, "users")).isFalse();
    }

    @Test
    void detectWildcardColumn() {
        ColumnReference wildcard = ColumnReference.of("users", "*");
        assertThat(ColumnReferenceUtil.isWildcard(wildcard)).isTrue();
    }

    @Test
    void detectNonWildcardColumn() {
        ColumnReference colRef = ColumnReference.of("users", "name");
        assertThat(ColumnReferenceUtil.isWildcard(colRef)).isFalse();
    }

    @Test
    void cannotInstantiateUtilityClass() throws NoSuchMethodException {
        Constructor<ColumnReferenceUtil> constructor = ColumnReferenceUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(UnsupportedOperationException.class);
    }
}
