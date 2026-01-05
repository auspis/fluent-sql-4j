package io.github.auspis.fluentsql4j.ast.visitor;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.dql.clause.Fetch;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ContextPreparationVisitorFetchTest {

    private ContextPreparationVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new ContextPreparationVisitor();
    }

    @Test
    void fetchClauseDoesNotAddNewFeatures() {
        SelectStatement statement = SelectStatement.builder()
                .from(From.fromTable("users"))
                .fetch(Fetch.of(10))
                .build();

        AstContext result = statement.accept(visitor, new AstContext());

        assertThat(result.features()).isEmpty();
    }
}
