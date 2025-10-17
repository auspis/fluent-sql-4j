package lan.tlab.r4j.sql.plugin.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class VersionMatcherTest {

    @Test
    void caretRangeMatchesCompatibleVersions() {
        assertThat(VersionMatcher.matches("14.0.0", "^14.0.0")).isTrue();
        assertThat(VersionMatcher.matches("14.5.2", "^14.0.0")).isTrue();
        assertThat(VersionMatcher.matches("14.9.99", "^14.0.0")).isTrue();
    }

    @Test
    void caretRangeRejectsIncompatibleMajorVersion() {
        assertThat(VersionMatcher.matches("15.0.0", "^14.0.0")).isFalse();
        assertThat(VersionMatcher.matches("13.9.9", "^14.0.0")).isFalse();
    }

    @Test
    void tildeRangeMatchesPatchUpdates() {
        assertThat(VersionMatcher.matches("5.7.0", "~5.7.0")).isTrue();
        assertThat(VersionMatcher.matches("5.7.9", "~5.7.0")).isTrue();
    }

    @Test
    void tildeRangeRejectsMinorVersionChanges() {
        assertThat(VersionMatcher.matches("5.8.0", "~5.7.0")).isFalse();
        assertThat(VersionMatcher.matches("5.6.9", "~5.7.0")).isFalse();
    }

    @Test
    void explicitRangeWorks() {
        assertThat(VersionMatcher.matches("8.0.35", ">=8.0.0 <9.0.0")).isTrue();
        assertThat(VersionMatcher.matches("8.5.0", ">=8.0.0 <9.0.0")).isTrue();
        assertThat(VersionMatcher.matches("9.0.0", ">=8.0.0 <9.0.0")).isFalse();
    }

    @Test
    void exactVersionMatches() {
        assertThat(VersionMatcher.matches("8.0.35", "8.0.35")).isTrue();
        assertThat(VersionMatcher.matches("8.0.36", "8.0.35")).isFalse();
    }

    @Test
    void throwsExceptionForNullVersion() {
        assertThatThrownBy(() -> VersionMatcher.matches(null, "^14.0.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Version must not be null");
    }

    @Test
    void throwsExceptionForNullRange() {
        assertThatThrownBy(() -> VersionMatcher.matches("14.0.0", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Version range must not be null");
    }

    @Test
    void throwsExceptionForInvalidVersionFormat() {
        assertThatThrownBy(() -> VersionMatcher.matches("invalid", "^14.0.0"))
                .isInstanceOf(VersionFormatException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void throwsExceptionForInvalidRangeFormat() {
        assertThatThrownBy(() -> VersionMatcher.matches("14.0.0", "invalid-range"))
                .isInstanceOf(VersionFormatException.class)
                .hasMessageContaining("Invalid version or range");
    }

    @Test
    void validatesCorrectVersionStrings() {
        assertThat(VersionMatcher.isValidVersion("14.0.0")).isTrue();
        assertThat(VersionMatcher.isValidVersion("8.0.35")).isTrue();
        assertThat(VersionMatcher.isValidVersion("1.0.0-beta")).isTrue();
        assertThat(VersionMatcher.isValidVersion("2.1.3-alpha.1")).isTrue();
    }

    @Test
    void rejectsInvalidVersionStrings() {
        assertThat(VersionMatcher.isValidVersion("invalid")).isFalse();
        assertThat(VersionMatcher.isValidVersion("^14.0.0")).isFalse(); // range, not version
        assertThat(VersionMatcher.isValidVersion(null)).isFalse();
        assertThat(VersionMatcher.isValidVersion("")).isFalse();
        assertThat(VersionMatcher.isValidVersion("   ")).isFalse();
    }
}
