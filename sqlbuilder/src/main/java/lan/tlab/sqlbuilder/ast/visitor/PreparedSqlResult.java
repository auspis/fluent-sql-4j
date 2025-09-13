package lan.tlab.sqlbuilder.ast.visitor;

import java.util.List;

public record PreparedSqlResult(String sql, List<Object> parameters) {}
