package lan.tlab.r4j.sql.plugin.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class SemVerUtilTest {

    @Test
    void matches_exactVersionMatches() {
        assertThat(SemVerUtil.matches("8.0.35", "8.0.35")).isTrue();
        assertThat(SemVerUtil.matches("8.0.36", "8.0.35")).isFalse();
    }

    @Test
    void matches_caretRangeMatchesCompatibleVersions() {
        assertThat(SemVerUtil.matches("14.0.0", "^14.0.0")).isTrue();
        assertThat(SemVerUtil.matches("14.5.2", "^14.0.0")).isTrue();
        assertThat(SemVerUtil.matches("14.9.99", "^14.0.0")).isTrue();
    }

    @Test
    void matches_caretRangeRejectsIncompatibleMajorVersion() {
        assertThat(SemVerUtil.matches("15.0.0", "^14.0.0")).isFalse();
        assertThat(SemVerUtil.matches("13.9.9", "^14.0.0")).isFalse();
    }

    @Test
    void matches_tildeRangeMatchesPatchUpdates() {
        assertThat(SemVerUtil.matches("5.7.0", "~5.7.0")).isTrue();
        assertThat(SemVerUtil.matches("5.7.9", "~5.7.0")).isTrue();
    }

    @Test
    void matches_tildeRangeRejectsMinorVersionChanges() {
        assertThat(SemVerUtil.matches("5.8.0", "~5.7.0")).isFalse();
        assertThat(SemVerUtil.matches("5.6.9", "~5.7.0")).isFalse();
    }

    @Test
    void matches_explicitRangeWorks() {
        assertThat(SemVerUtil.matches("8.0.35", ">=8.0.0 <9.0.0")).isTrue();
        assertThat(SemVerUtil.matches("8.5.0", ">=8.0.0 <9.0.0")).isTrue();
        assertThat(SemVerUtil.matches("9.0.0", ">=8.0.0 <9.0.0")).isFalse();
    }

    @Test
    void matches_throwsExceptionForNullVersion() {
        assertThatThrownBy(() -> SemVerUtil.matches(null, "^14.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void matches_throwsExceptionForNullRange() {
        assertThatThrownBy(() -> SemVerUtil.matches("14.0.0", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void matches_throwsExceptionForInvalidVersionFormat() {
        assertThatThrownBy(() -> SemVerUtil.matches("invalid", "^14.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void matches_throwsExceptionForInvalidRangeFormat() {
        assertThatThrownBy(() -> SemVerUtil.matches("14.0.0", "invalid-range"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void isValidVersion_validatesCorrectVersionStrings() {
        assertThat(SemVerUtil.isValidVersion("14.0.0")).isTrue();
        assertThat(SemVerUtil.isValidVersion("8.0.35")).isTrue();
        assertThat(SemVerUtil.isValidVersion("1.0.0-beta")).isTrue();
        assertThat(SemVerUtil.isValidVersion("2.1.3-alpha.1")).isTrue();
    }

    @Test
    void isValidVersion_rejectsInvalidVersionStrings() {
        assertThat(SemVerUtil.isValidVersion("invalid")).isFalse();
        assertThat(SemVerUtil.isValidVersion("^14.0.0")).isFalse(); // range, not version
        assertThat(SemVerUtil.isValidVersion(null)).isFalse();
        assertThat(SemVerUtil.isValidVersion("")).isFalse();
        assertThat(SemVerUtil.isValidVersion("   ")).isFalse();
    }

    @Test
    void isValidRange_validatesCorrectRangeStrings() {
        assertThat(SemVerUtil.isValidRange("^14.0.0")).isTrue();
        assertThat(SemVerUtil.isValidRange("~5.7.0")).isTrue();
        assertThat(SemVerUtil.isValidRange(">=8.0.0 <9.0.0")).isTrue();
        assertThat(SemVerUtil.isValidRange("8.0.0")).isTrue(); // exact version is valid range
    }

    @Test
    void isValidRange_rejectsInvalidRangeStrings() {
        assertThat(SemVerUtil.isValidRange("invalid")).isFalse();
        assertThat(SemVerUtil.isValidRange(null)).isFalse();
        assertThat(SemVerUtil.isValidRange("   ")).isFalse();
    }
}
