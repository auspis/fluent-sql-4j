# Build Hook Guide

This guide explains how to:

1. Enable SQL build logging.
2. Configure hooks programmatically (without system properties).
3. Create and activate custom `BuildHook` providers.
4. Validate discovery on both module-path and classpath (Spring Boot style runtime).

## Overview

Build hooks run during `PreparedStatementSpecFactory.create(...)`, around AST rendering.

Lifecycle callbacks:

- `before(statement)`
- `onSuccess(spec)`
- `onError(error)`

The hook base class uses a template method approach and swallows hook-internal errors so hook failures do not break SQL build flow.

## Configuration Modes

You can configure hooks in two complementary ways:

- Property-based + ServiceLoader discovery
- Programmatic injection via `DSLRegistry.create(BuildHookFactory)`

Both modes are valid and can be combined.

## Enable Built-In Logging Hook

The built-in provider is `LoggingBuildHookProvider`.

### Option A: Property-based configuration (ServiceLoader)

Use this when you want environment-driven configuration.

Supported properties:

- `fluentsql.hooks.build.logging.enabled` (default: `false`)
- `fluentsql.hooks.build.logging.level` (default: `DEBUG`)
- `fluentsql.hooks.build.logging.includeParams` (default: `false`)

### Example (JVM args)

```bash
-Dfluentsql.hooks.build.logging.enabled=true \
-Dfluentsql.hooks.build.logging.level=INFO \
-Dfluentsql.hooks.build.logging.includeParams=true
```

Example with `System.setProperty(...)`:

Set properties before creating DSL/registry instances:

```java
System.setProperty("fluentsql.hooks.build.logging.enabled", "true");
System.setProperty("fluentsql.hooks.build.logging.level", "INFO");
System.setProperty("fluentsql.hooks.build.logging.includeParams", "true");

DSLRegistry registry = DSLRegistry.createWithServiceLoader();
DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
```

Note: in the ServiceLoader path, providers are configured from `System.getProperties()` when `BuildHookFactory.create()` runs.

### Option B: Programmatic configuration (no system properties)

Use this when the host application wants explicit in-code wiring.

```java
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHook;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

BuildHookFactory loggingFactory = () -> new LoggingBuildHook(
    LoggerFactory.getLogger("sql-build"),
    Level.INFO,
    true
);

DSLRegistry registry = DSLRegistry.create(loggingFactory);
DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME).orElseThrow();
```

### Option C: Combine SPI hooks and programmatic hooks

```java
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import io.github.auspis.fluentsql4j.hook.build.ServiceLoaderBuildHookFactory;

BuildHookFactory combined = BuildHookFactory.composite(
    new ServiceLoaderBuildHookFactory(),
    () -> new MetricsCounterHook(metricsRegistry)
);

DSLRegistry registry = DSLRegistry.create(combined);
```

## Create a Custom BuildHook Provider

### 1. Implement the hook

```java
package com.example.hooks;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

public final class AuditBuildHook extends BuildHook {

    @Override
    protected void doBefore(Statement statement) {
        // custom audit start
    }

    @Override
    protected void doOnSuccess(PreparedStatementSpec spec) {
        // custom audit success
    }

    @Override
    protected void doOnError(Throwable error) {
        // custom audit failure
    }
}
```

### 2. Implement provider SPI (updated contract)

```java
package com.example.hooks;

import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookProvider;
import java.util.Properties;

public final class AuditBuildHookProvider implements BuildHookProvider {

    private boolean enabled;

    @Override
    public String id() {
        return "audit";
    }

    @Override
    public int order() {
        return 200;
    }

    @Override
    public BuildHookProvider configure(Properties properties) {
        this.enabled = Boolean.parseBoolean(
                properties.getProperty("fluentsql.hooks.build.audit.enabled", "false"));
        return this;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public BuildHook create() {
        return new AuditBuildHook();
    }
}
```

For purely programmatic providers, put all configuration in the constructor and leave `configure(Properties)` as default no-op.

### 3. Programmatic-only custom hook (no SPI registration)

If you do not need auto-discovery, you can skip `BuildHookProvider` entirely:

```java
BuildHookFactory factory = () -> new AuditBuildHook();
DSLRegistry registry = DSLRegistry.create(factory);
```

### 4. Register provider for classpath

Create this file in your module/JAR:

`META-INF/services/io.github.auspis.fluentsql4j.hook.build.BuildHookProvider`

With content:

```text
com.example.hooks.AuditBuildHookProvider
```

### 5. Register provider for JPMS (module-path)

In `module-info.java`:

```java
uses io.github.auspis.fluentsql4j.hook.build.BuildHookProvider;

provides io.github.auspis.fluentsql4j.hook.build.BuildHookProvider with
        com.example.hooks.AuditBuildHookProvider;
```

## Automatic Discovery

Yes: once your provider is correctly registered and your JAR is on the runtime classpath/module-path, it is discovered automatically via `ServiceLoader`.

No manual registry wiring is required.

## Validation

Run strict classpath SPI checks:

```bash
./mvnw test -Pclasspath-strict -Dsurefire.failIfNoSpecifiedTests=false
```

This validates classpath mode behavior (common in Spring Boot deployments).
