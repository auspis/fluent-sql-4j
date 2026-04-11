package io.github.auspis.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import org.junit.jupiter.api.Test;

class DSLWithBuildHookFactoryTest {

    private final PreparedStatementSpecFactory baseSpecFactory = new PreparedStatementSpecFactory(
            AstToPreparedStatementSpecVisitor.builder().build());

    @Test
    void returnsDslWithNewHookFactory() {
        DSL original = new DSL(baseSpecFactory);
        BuildHookFactory newFactory = BuildHook::nullObject;

        DSL result = original.withBuildHookFactory(newFactory);

        assertThat(result).isNotSameAs(original);
        assertThat(result.getSpecFactory().buildHookFactory()).isSameAs(newFactory);
    }

    @Test
    void preservesAstVisitor() {
        DSL original = new DSL(baseSpecFactory);
        BuildHookFactory newFactory = BuildHook::nullObject;

        DSL result = original.withBuildHookFactory(newFactory);

        assertThat(result.getSpecFactory().astVisitor())
                .isSameAs(original.getSpecFactory().astVisitor());
    }

    @Test
    void originalDslIsUnchanged() {
        DSL original = new DSL(baseSpecFactory);
        BuildHookFactory newFactory = BuildHook::nullObject;

        original.withBuildHookFactory(newFactory);

        assertThat(original.getSpecFactory()).isSameAs(baseSpecFactory);
    }

    @Test
    void nullHookFactoryThrows() {
        DSL dsl = new DSL(baseSpecFactory);

        assertThatThrownBy(() -> dsl.withBuildHookFactory(null)).isInstanceOf(NullPointerException.class);
    }
}
