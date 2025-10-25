package lan.tlab.r4j.sql.ast.statement.dml.item;

import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public sealed interface MergeUsing extends Visitable permits MergeUsing.TableSource, MergeUsing.SubquerySource {

    record TableSource(TableIdentifier table) implements MergeUsing {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }

    record SubquerySource(SelectStatement subquery, Alias alias) implements MergeUsing {
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return visitor.visit(this, ctx);
        }
    }
}
