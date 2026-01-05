package io.github.auspis.fluentsql4j.dsl.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public class PsUtil {
    private PsUtil() {}

    public static PreparedStatement preparedStatement(PreparedStatementSpec spec, Connection connection)
            throws SQLException {
        PreparedStatement ps = connection.prepareStatement(spec.sql());
        for (int i = 0; i < spec.parameters().size(); i++) {
            ps.setObject(i + 1, spec.parameters().get(i));
        }
        return ps;
    }
}
