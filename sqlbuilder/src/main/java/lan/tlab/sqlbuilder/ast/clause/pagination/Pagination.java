package lan.tlab.sqlbuilder.ast.clause.pagination;

import java.util.Objects;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Pagination implements Clause {

    @Default
    private final Integer page = 0;

    private final Integer perPage;

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public boolean isActive() {
        return Objects.isNull(perPage);
    }
}
