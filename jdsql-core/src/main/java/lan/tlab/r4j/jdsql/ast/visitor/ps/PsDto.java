package lan.tlab.r4j.jdsql.ast.visitor.ps;

import java.util.List;

public record PsDto(String sql, List<Object> parameters) {

    public PsDto() {
        this("", List.of());
    }
}
