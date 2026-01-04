package io.github.massimiliano.fluentsql4j.ast.visitor.ps;

import java.util.List;

public record PreparedStatementSpec(String sql, List<Object> parameters) {

    public PreparedStatementSpec() {
        this("", List.of());
    }
}
