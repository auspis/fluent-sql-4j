package lan.tlab.sqlbuilder.ast.expression.item;

import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Table implements SqlItem, TableExpression, FromSource {

    private String name = "";
    private As as = As.nullObject();

    public Table(String name) {
        this(name, As.nullObject());
    }

    public Table(String name, String alias) {
        this(name, new As(alias));
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
