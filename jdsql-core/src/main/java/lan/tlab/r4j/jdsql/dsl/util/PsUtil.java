package lan.tlab.r4j.jdsql.dsl.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;

public class PsUtil {
    private PsUtil() {}

    public static PreparedStatement preparedStatement(PsDto psDto, Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(psDto.sql());
        for (int i = 0; i < psDto.parameters().size(); i++) {
            ps.setObject(i + 1, psDto.parameters().get(i));
        }
        return ps;
    }
}
