package lan.tlab.r4j.sql.plugin.util;

/**
 * Exception thrown when a version or version range string is not in valid semantic versioning format.
 * <p>
 * This exception is typically thrown by {@link VersionMatcher} when parsing version strings
 * or version ranges that do not conform to the semantic versioning specification.
 */
public class VersionFormatException extends RuntimeException {

    /**
     * Constructs a new version format exception with the specified detail message.
     *
     * @param message the detail message
     */
    public VersionFormatException(String message) {
        super(message);
    }

    /**
     * Constructs a new version format exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public VersionFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
