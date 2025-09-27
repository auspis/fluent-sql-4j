package lan.tlab.sqlbuilder.ast.clause.pagination;

import java.util.Objects;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
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
    private final Integer offset = 0;

    private final Integer rows;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public boolean isActive() {
        return !Objects.isNull(rows);
    }
}
