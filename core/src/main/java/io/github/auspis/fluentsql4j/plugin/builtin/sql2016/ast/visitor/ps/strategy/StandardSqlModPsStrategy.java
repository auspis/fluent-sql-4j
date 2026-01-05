package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import io.github.auspis.fluentsql4j.ast.core.expression.function.number.Mod;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ModPsStrategy;

public class StandardSqlModPsStrategy implements ModPsStrategy {

    @Override
    public PreparedStatementSpec handle(Mod mod, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        PreparedStatementSpec dividendResult = mod.dividend().accept(astToPsSpecVisitor, ctx);
        PreparedStatementSpec divisorResult = mod.divisor().accept(astToPsSpecVisitor, ctx);

        List<Object> parameters = new ArrayList<>();
        parameters.addAll(dividendResult.parameters());
        parameters.addAll(divisorResult.parameters());

        String sql = String.format("MOD(%s, %s)", dividendResult.sql(), divisorResult.sql());

        return new PreparedStatementSpec(sql, parameters);
    }
}
