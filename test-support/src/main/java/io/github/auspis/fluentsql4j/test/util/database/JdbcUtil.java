package io.github.auspis.fluentsql4j.test.util.database;

import io.github.auspis.fluentsql4j.test.util.database.DataUtil.CartItemRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.CustomerRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.OrderRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.ProductRecord;
import io.github.auspis.fluentsql4j.test.util.database.DataUtil.UserRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;

final class JdbcUtil {
    private JdbcUtil() {}

    static void bind(PreparedStatement ps, UserRecord user) throws SQLException {
        ps.setLong(1, user.id());
        ps.setString(2, user.name());
        ps.setString(3, user.email());
        ps.setInt(4, user.age());
        ps.setBoolean(5, user.active());
        ps.setDate(6, java.sql.Date.valueOf(LocalDate.parse(user.birthdate())));
        ps.setTimestamp(7, Timestamp.valueOf(LocalDate.parse(user.createdAt()).atStartOfDay()));
        ps.setString(8, user.address());
        ps.setString(9, user.preferences());
        ps.executeUpdate();
    }

    static void bind(PreparedStatement ps, ProductRecord product) throws SQLException {
        ps.setLong(1, product.id());
        ps.setString(2, product.name());
        ps.setDouble(3, product.price());
        ps.setInt(4, product.quantity());
        ps.setString(5, product.metadata());
        ps.executeUpdate();
    }

    static void bind(PreparedStatement ps, OrderRecord order) throws SQLException {
        ps.setLong(1, order.id());
        ps.setLong(2, order.userId());
        ps.setDouble(3, order.total());
        ps.executeUpdate();
    }

    static void bind(PreparedStatement ps, CartItemRecord item) throws SQLException {
        ps.setLong(1, item.cartId());
        ps.setLong(2, item.productId());
        ps.setString(3, item.productName());
        ps.setDouble(4, item.unitPrice());
        ps.setInt(5, item.quantity());
        ps.executeUpdate();
    }

    static void bind(PreparedStatement ps, CustomerRecord customer) throws SQLException {
        ps.setInt(1, customer.id());
        ps.setString(2, customer.name());
        ps.setString(3, customer.country());
        ps.executeUpdate();
    }

    static void executeSql(Connection connection, String sql) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }
}
