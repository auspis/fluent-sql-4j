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

    @Test
    void createWithTableReferenceAndValidColumn() {
        ColumnReference result = ColumnReferenceUtil.createWithTableReference("users", "name");

        assertThat(result.table()).isEqualTo("users");
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void createWithNullTableReference() {
        ColumnReference result = ColumnReferenceUtil.createWithTableReference(null, "name");

        assertThat(result.table()).isEmpty();
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void createWithEmptyTableReference() {
        ColumnReference result = ColumnReferenceUtil.createWithTableReference("", "name");

        assertThat(result.table()).isEmpty();
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void createWithTableReferenceThrowsOnNullColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createWithTableReference("users", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createWithTableReferenceThrowsOnEmptyColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createWithTableReference("users", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createWithTableReferenceThrowsOnWhitespaceColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createWithTableReference("users", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    // Tests for createValidated(String table, String column)
    @Test
    void createValidatedWithValidTableAndColumn() {
        ColumnReference result = ColumnReferenceUtil.createValidated("users", "name");

        assertThat(result.table()).isEqualTo("users");
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void createValidatedThrowsOnNullTable() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated(null, "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table reference cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnEmptyTable() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("", "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table reference cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnWhitespaceTable() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("   ", "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table reference cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnTableWithDot() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("schema.users", "name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table reference must not contain dot: 'schema.users'");
    }

    @Test
    void createValidatedThrowsOnNullColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("users", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnEmptyColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("users", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnWhitespaceColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("users", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedThrowsOnColumnWithDot() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("users", "table.name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot");
    }

    // Tests for createValidated(String column) - column only
    @Test
    void createValidatedColumnOnlyWithValidColumn() {
        ColumnReference result = ColumnReferenceUtil.createValidated("name");

        assertThat(result.table()).isEmpty();
        assertThat(result.column()).isEqualTo("name");
    }

    @Test
    void createValidatedColumnOnlyThrowsOnNullColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedColumnOnlyThrowsOnEmptyColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedColumnOnlyThrowsOnWhitespaceColumn() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name cannot be null or empty");
    }

    @Test
    void createValidatedColumnOnlyThrowsOnColumnWithDot() {
        assertThatThrownBy(() -> ColumnReferenceUtil.createValidated("table.name"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Column name must not contain dot notation: 'table.name'");
    }
}
