package io.github.auspis.fluentsql4j.plugin.util;

import org.semver4j.Semver;
import org.semver4j.SemverException;
import org.semver4j.range.RangeList;
import org.semver4j.range.RangeListFactory;

/**
 * Utility class for semantic version matching operations.
 * <p>
 * This class provides a wrapper around the semver4j library to isolate
 * the dependency and provide a more specific API for SQL dialect version matching.
 * It supports NPM-style version ranges like "^8.0.0", "~5.7.0", ">=14.0.0 <15.0.0", etc.
 * <p>
 * This is a utility class and cannot be instantiated.
 *
 * @see <a href="https://github.com/semver4j/semver4j">semver4j library</a>
 */
public final class SemVerUtil {

    private SemVerUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Checks if a version satisfies a version range.
     * <p>
     * Supports NPM-style version ranges:
     * <ul>
     *   <li>Caret ranges: "^8.0.0" (>=8.0.0 <9.0.0)</li>
     *   <li>Tilde ranges: "~5.7.0" (>=5.7.0 <5.8.0)</li>
     *   <li>Explicit ranges: ">=14.0.0 <15.0.0"</li>
     *   <li>Exact versions: "8.0.35"</li>
     * </ul>
     * <p>
     * <b>Examples:</b>
     * <pre>{@code
     * // Caret range - compatible versions
     * matches("14.0.0", "^14.0.0")  // true
     * matches("14.5.2", "^14.0.0")  // true
     * matches("15.0.0", "^14.0.0")  // false (major version changed)
     *
     * // Tilde range - patch updates only
     * matches("5.7.0", "~5.7.0")    // true
     * matches("5.7.9", "~5.7.0")    // true
     * matches("5.8.0", "~5.7.0")    // false (minor version changed)
     *
     * // Explicit range
     * matches("8.0.35", ">=8.0.0 <9.0.0")  // true
     * }</pre>
     *
     * @param version the version to check (e.g., "14.0.0", "8.0.35")
     * @param versionRange the version range to match against (e.g., "^14.0.0", "~5.7.0")
     * @return {@code true} if the version satisfies the range, {@code false} otherwise
     * @throws IllegalArgumentException if version or versionRange are invalid (including {@code null})
     */
    public static boolean matches(String version, String versionRange) {
        try {
            Semver semver = Semver.parse(version);
            return semver.satisfies(versionRange);
        } catch (SemverException | NullPointerException e) {
            throw new IllegalArgumentException(
                    "Invalid version or range: version='" + version + "', range='" + versionRange + "'", e);
        }
    }

    /**
     * Validates that a version string is a valid semantic version.
     * <p>
     * <b>Examples:</b>
     * <pre>{@code
     * isValidVersion("14.0.0")     // true
     * isValidVersion("8.0.35")     // true
     * isValidVersion("1.0.0-beta") // true
     * isValidVersion("invalid")    // false
     * isValidVersion("^14.0.0")    // false (range, not version)
     * }</pre>
     *
     * @param version the version string to validate
     * @return {@code true} if valid, {@code false} otherwise
     */
    public static boolean isValidVersion(String version) {
        if (version == null || version.trim().isEmpty()) {
            return false;
        }
        return Semver.isValid(version);
    }

    /**
     * Validates that a version range string is a valid NPM-style range.
     * <p>
     * <b>Examples:</b>
     * <pre>{@code
     * isValidRange("^14.0.0")           // true
     * isValidRange("~5.7.0")            // true
     * isValidRange(">=8.0.0 <9.0.0")    // true
     * isValidRange("8.0.0")             // true (exact version is a valid range)
     * isValidRange("invalid")           // false
     * }</pre>
     *
     * @param versionRange the version range string to validate
     * @return {@code true} if valid, {@code false} otherwise
     */
    public static boolean isValidRange(String versionRange) {
        if (versionRange == null || versionRange.trim().isEmpty()) {
            return false;
        }
        RangeList rangeList = RangeListFactory.create(versionRange);
        return rangeList.get().size() > 0;
    }
}
