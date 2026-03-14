package io.github.auspis.fluentsql4j.test.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Utility helpers for concise scalar DB assertions in integration tests.
 */
public final class QueryUtil {

    private QueryUtil() {
        // utility class
    }

    public static long countByColumn(Connection connection, String tableName, String columnName, Object value)
            throws SQLException {
        requireConnection(connection);
        String table = requireIdentifier(tableName, "tableName");
        String column = requireIdentifier(columnName, "columnName");

        String sql = "SELECT COUNT(*) FROM " + quoteIdentifier(table) + " WHERE " + quoteIdentifier(column) + " = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public static long countByColumnIn(Connection connection, String tableName, String columnName, Object... values)
            throws SQLException {
        requireConnection(connection);
        String table = requireIdentifier(tableName, "tableName");
        String column = requireIdentifier(columnName, "columnName");
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("At least one value must be provided");
        }

        StringJoiner placeholders = new StringJoiner(", ");
        for (int i = 0; i < values.length; i++) {
            placeholders.add("?");
        }

        String sql = "SELECT COUNT(*) FROM " + quoteIdentifier(table) + " WHERE " + quoteIdentifier(column) + " IN ("
                + placeholders + ")";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    public static boolean existsByColumn(Connection connection, String tableName, String columnName, Object value)
            throws SQLException {
        return countByColumn(connection, tableName, columnName, value) > 0;
    }

    public static <T> Optional<T> getSingleValueByColumn(
            Connection connection,
            String tableName,
            String selectedColumn,
            String whereColumn,
            Object whereValue,
            Class<T> type)
            throws SQLException {
        requireConnection(connection);
        String table = requireIdentifier(tableName, "tableName");
        String selected = requireIdentifier(selectedColumn, "selectedColumn");
        String where = requireIdentifier(whereColumn, "whereColumn");
        Objects.requireNonNull(type, "type must not be null");

        String sql = "SELECT " + quoteIdentifier(selected) + " FROM " + quoteIdentifier(table) + " WHERE "
                + quoteIdentifier(where) + " = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, whereValue);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Object raw = rs.getObject(1);
                if (raw == null) {
                    return Optional.empty();
                }
                return Optional.of(convertValue(raw, type));
            }
        }
    }

    private static <T> T convertValue(Object raw, Class<T> type) {
        if (type.isInstance(raw)) {
            return type.cast(raw);
        }

        if (raw instanceof Number number) {
            if (type == Long.class) {
                return type.cast(number.longValue());
            }
            if (type == Integer.class) {
                return type.cast(number.intValue());
            }
            if (type == Short.class) {
                return type.cast(number.shortValue());
            }
            if (type == Byte.class) {
                return type.cast(number.byteValue());
            }
            if (type == Double.class) {
                return type.cast(number.doubleValue());
            }
            if (type == Float.class) {
                return type.cast(number.floatValue());
            }
        }

        throw new IllegalStateException(
                "Cannot cast value of type " + raw.getClass().getName() + " to " + type.getName());
    }

    private static void requireConnection(Connection connection) {
        Objects.requireNonNull(connection, "connection must not be null");
    }

    private static String requireIdentifier(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
        return value;
    }

    private static String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }
}
