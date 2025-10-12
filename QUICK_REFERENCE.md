# Plugin Architecture Quick Reference

## Document Overview

This directory contains complete documentation for implementing a plugin architecture for SQL dialects in the r4j framework.

## Documents

### 1. PLUGIN_ARCHITECTURE_ISSUES.md

**Purpose:** Detailed breakdown of all GitHub issues needed for implementation  
**Audience:** Project managers, developers implementing features  
**Contents:**
- 18 detailed issues organized in 8 phases
- Each issue includes acceptance criteria, technical notes, dependencies, and effort estimates
- Implementation order recommendations
- Sprint planning suggestions

**When to use:** When planning the project, creating GitHub issues, or implementing features

### 2. GITHUB_ISSUES_TEMPLATE.md

**Purpose:** Ready-to-use templates for creating GitHub issues  
**Audience:** Project managers, team leads  
**Contents:**
- Copy-paste templates for each issue
- Detailed descriptions, acceptance criteria, code examples
- Labels, milestones, and dependency information
- Instructions for creating issues in GitHub

**When to use:** When actually creating issues in GitHub

### 3. ARCHITECTURE_OVERVIEW.md

**Purpose:** Visual and conceptual overview of the plugin architecture  
**Audience:** All stakeholders, new developers, architects  
**Contents:**
- Architecture diagrams
- Component descriptions
- Data flow examples
- Package structure
- ServiceLoader (SPI) explanation
- Benefits and migration path
- Performance considerations
- Testing strategy

**When to use:** When understanding the big picture, onboarding new developers, or making architectural decisions

### 4. QUICK_REFERENCE.md (This Document)

**Purpose:** Navigation guide and quick lookups  
**Audience:** Everyone  
**Contents:**
- Document summaries
- Quick facts
- Key decisions
- Common questions

## Quick Facts

### Project Statistics

- **Total Issues:** 18
- **Minimum Viable Product:** 10 issues (Issues 1-10, 13)
- **Complete Initial Release:** 14 issues (Issues 1-14)
- **Enhanced Release:** All 18 issues
- **Estimated Total Effort:** 50-65 hours

### Implementation Phases

1. **Phase 1 - Core Infrastructure:** Issues 1-2 (4-6 hours)
2. **Phase 2 - Built-in Plugins:** Issues 3-6, 8 (10-13 hours)
3. **Phase 3 - DSL Integration:** Issues 9-10 (6-8 hours)
4. **Phase 4 - Documentation:** Issues 11-12 (6-7 hours)
5. **Phase 5 - Testing:** Issues 13-14 (9-12 hours)
6. **Phase 6 - Optimization:** Issue 15 (3-4 hours)
7. **Phase 7 - Advanced Features:** Issues 16-18 (17-23 hours)

### Core Components

#### SqlDialectPlugin (Interface)

```java
package: lan.tlab.r4j.sql.dsl.plugin
methods: getDialectName(), getVersion(), createRenderer(), supports(), getSupportedFeatures()
```

#### SqlDialectRegistry (Registry)

```java
package: lan.tlab.r4j.sql.dsl.plugin
purpose: Manages plugins, uses ServiceLoader for auto-discovery
thread-safety: ConcurrentHashMap
```

#### Built-in Plugins

- StandardSQLDialectPlugin (sql2008, standard, ansi)
- MySQLDialectPlugin (mysql, mariadb)
- PostgreSQLDialectPlugin (postgresql, postgres, pg)
- SqlServerDialectPlugin (sqlserver, mssql, tsql)

#### DSL Class (Refactored)

```java
package: lan.tlab.r4j.sql.dsl
new features: Instance-based API with dialect-specific renderers
backward compatibility: All static methods remain unchanged
```

### Key Technical Decisions

1. **ServiceLoader for Plugin Discovery**
   - Standard Java mechanism (SPI)
   - Automatic discovery at startup
   - No manual registration code needed
2. **Backward Compatibility is Mandatory**
   - All existing static methods must work unchanged
   - Existing tests must pass without modification
   - Static methods delegate to standard() dialect
3. **Thread Safety**
   - ConcurrentHashMap for plugin storage
   - Immutable DSL instances
   - Static initialization of registry
4. **Case-Insensitive Dialect Names**
   - Store as lowercase internally
   - Accept any case from users
   - Example: MySQL, mysql, MYSQL all work
5. **Plugin Aliases Support**
   - Each plugin can support multiple names
   - Example: postgres, postgresql, pg → same plugin
   - Implemented via supports() method
6. **No Breaking Changes**
   - Existing SqlRendererFactory unchanged
   - Existing builders unchanged
   - Only DSL class gets new features

## Common Questions

### Q: Do I need to modify existing code to use this?

**A:** No. All existing code continues to work unchanged. The plugin architecture adds new capabilities while maintaining full backward compatibility.

### Q: How do I create an external plugin?

**A:**
1. Implement SqlDialectPlugin interface
2. Create META-INF/services configuration file
3. Package as JAR
4. Add to classpath
5. Plugin is automatically discovered

### Q: What if I want to use a specific dialect?

**A:**

```java
// Old way (still works, uses standard SQL)
DSL.select("name").from("users").build();

// New way (dialect-specific)
DSL mysql = DSL.mysql();
mysql.select("name").from("users").build();

// Or dynamically
DSL dsl = DSL.forDialect("mysql");
```

### Q: Can I have multiple dialects in the same application?

**A:** Yes! Create separate DSL instances:

```java
DSL mysql = DSL.mysql();
DSL postgres = DSL.postgresql();

String mysqlSql = mysql.select("name").from("users").build();
String postgresSql = postgres.select("name").from("users").build();
```

### Q: How do I know which dialects are available?

**A:**

```java
Set<String> dialects = DSL.getSupportedDialects();
// Returns: [sql2008, mysql, postgresql, sqlserver]
```

### Q: What happens if I request an unsupported dialect?

**A:** IllegalArgumentException with helpful message:

```
"Unsupported SQL dialect: oracle. Supported dialects: [sql2008, mysql, postgresql, sqlserver]"
```

### Q: Is there any performance overhead?

**A:** Minimal:
- Plugin loading happens once at startup (static initialization)
- Renderer creation delegates to existing factory methods
- Plugin lookup is O(1) HashMap access
- No overhead after initialization

### Q: Do I need to modify builder classes?

**A:** No. All builder classes already accept SqlRenderer in their constructors. Issue #10 is just verification.

### Q: Can plugins be distributed as separate JARs?

**A:** Yes. Each plugin can be a separate JAR with its own SPI configuration. Users include only the dialects they need.

### Q: What about database-specific SQL features?

**A:** Each plugin declares supported features via getSupportedFeatures(). Future phases may add feature detection API (Issue #16).

### Q: How do I test my plugin?

**A:** See GITHUB_ISSUES_TEMPLATE.md Issue #12 for example plugin structure. Use standard JUnit tests with your plugin.

### Q: What about Oracle/H2/other databases?

**A:** The architecture supports any database. Create a plugin implementing SqlDialectPlugin interface. See Issue #7 for Oracle as an example.

## Implementation Checklist

### Sprint 1: Foundation

- [ ] Issue #1: SqlDialectPlugin interface
- [ ] Issue #2: SqlDialectRegistry
- [ ] Verify ServiceLoader mechanism works
- [ ] Create basic tests

### Sprint 2: Built-in Plugins

- [ ] Issue #3: StandardSQLDialectPlugin
- [ ] Issue #4: MySQLDialectPlugin
- [ ] Issue #5: PostgreSQLDialectPlugin
- [ ] Issue #6: SqlServerDialectPlugin
- [ ] Issue #8: META-INF/services configuration
- [ ] Test auto-discovery

### Sprint 3: DSL Integration

- [ ] Issue #9: Refactor DSL class
- [ ] Issue #10: Verify builders
- [ ] Run existing tests (should pass)
- [ ] Create backward compatibility tests

### Sprint 4: Documentation

- [ ] Issue #11: Create documentation
- [ ] Issue #12: Example external plugin
- [ ] Update README.md
- [ ] Create migration guide

### Sprint 5: Testing

- [ ] Issue #13: Integration tests
- [ ] Issue #14: Migration tests
- [ ] Test with Testcontainers
- [ ] Performance benchmarks

### Sprint 6: Optimization (Optional)

- [ ] Issue #15: Performance optimization
- [ ] Profile and benchmark
- [ ] Optimize if needed

### Sprint 7: Advanced Features (Optional)

- [ ] Issue #16: Feature detection API
- [ ] Issue #17: Plugin configuration
- [ ] Issue #18: Multi-module distribution

## Code Locations

### New Files to Create

```
sql/src/main/java/lan/tlab/r4j/sql/dsl/plugin/
  ├── SqlDialectPlugin.java                     [Issue #1]
  ├── SqlDialectRegistry.java                   [Issue #2]
  └── builtin/
      ├── StandardSQLDialectPlugin.java         [Issue #3]
      ├── MySQLDialectPlugin.java               [Issue #4]
      ├── PostgreSQLDialectPlugin.java          [Issue #5]
      └── SqlServerDialectPlugin.java           [Issue #6]

sql/src/main/resources/META-INF/services/
  └── lan.tlab.r4j.sql.dsl.plugin.SqlDialectPlugin  [Issue #8]

sql/src/test/java/lan/tlab/r4j/sql/dsl/plugin/
  ├── SqlDialectPluginTest.java
  ├── SqlDialectRegistryTest.java
  └── builtin/
      ├── StandardSQLDialectPluginTest.java
      ├── MySQLDialectPluginTest.java
      ├── PostgreSQLDialectPluginTest.java
      └── SqlServerDialectPluginTest.java

test-integration/src/test/java/lan/tlab/r4j/integration/sql/plugin/
  ├── PluginSystemIntegrationTest.java          [Issue #13]
  └── BackwardCompatibilityTest.java            [Issue #14]

docs/
  ├── PLUGIN_DEVELOPMENT.md                     [Issue #11]
  └── MIGRATION_GUIDE.md                        [Issue #11]

spike/ or examples/
  └── external-plugin-example/                  [Issue #12]
```

### Files to Modify

```
sql/src/main/java/lan/tlab/r4j/sql/dsl/DSL.java  [Issue #9]
README.md                                         [Issue #11]
```

## Testing Commands

```bash
# Set Java 21
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Format code (required before commit)
./mvnw spotless:apply

# Build
./mvnw clean compile

# Run unit tests
./mvnw test

# Run integration tests  
./mvnw verify

# Run specific module tests
./mvnw test -pl sql
./mvnw verify -pl test-integration

# Run specific test
./mvnw test -Dtest=SqlDialectRegistryTest

# Check for updates
./mvnw versions:display-dependency-updates
```

## Important Coding Standards

From `.github/copilot-instructions.md`:
- Java 21 required
- **Never use `var`** to declare variables
- Never use Java reflection to solve problems
- Use AssertJ for assertions
- Use JUnit 5 for testing
- Keep test names compact, avoid prefixes like "test", "with", "handle"
- Helper classes must be in `*.helper` package
- Utility classes must be in `*.util` package with specific requirements:
- Must be `final`
- Must have private no-arg constructor
- All methods must be `static`
- Cannot be instantiated

## Git Workflow

```bash
# Format before commit (MANDATORY)
./mvnw spotless:apply

# Check status
git status

# Stage changes
git add .

# Commit
git commit -m "Descriptive message"

# Push
git push
```

## Priority Matrix

### Must Have (MVP)

- Core plugin interface and registry (Issues 1-2)
- Built-in plugins for existing dialects (Issues 3-6, 8)
- DSL refactoring with backward compatibility (Issues 9-10)
- Basic integration tests (Issue 13)

### Should Have (Initial Release)

- Comprehensive documentation (Issues 11-12)
- Migration tests (Issue 14)

### Nice to Have (Enhanced Release)

- Performance optimization (Issue 15)
- Feature detection API (Issue 16)
- Plugin configuration (Issue 17)
- Multi-module distribution (Issue 18)

## Success Criteria

### Minimum Viable Product

- [ ] Plugin system works with all built-in dialects
- [ ] Backward compatibility: all existing tests pass unchanged
- [ ] Auto-discovery via ServiceLoader works
- [ ] Can use DSL.mysql(), DSL.postgresql(), etc.
- [ ] Can create external plugin with example

### Complete Initial Release

- [ ] All MVP criteria met
- [ ] Comprehensive documentation created
- [ ] Migration guide available
- [ ] Example external plugin created
- [ ] Integration tests with Testcontainers pass
- [ ] No performance regression

### Enhanced Release

- [ ] All initial release criteria met
- [ ] Performance optimized
- [ ] Feature detection API implemented
- [ ] Plugin configuration system available
- [ ] Multi-module distribution (optional)

## Contact & Support

For questions about this implementation:
1. Review the appropriate document (see Document Overview above)
2. Check Common Questions section
3. Review GitHub issues for specific technical details
4. Consult ARCHITECTURE_OVERVIEW.md for design decisions

## Next Steps

1. **Review:** Read all documents to understand the full scope
2. **Plan:** Decide which issues to implement (MVP, Complete, or Enhanced)
3. **Create Issues:** Use templates from GITHUB_ISSUES_TEMPLATE.md
4. **Implement:** Follow the implementation order in PLUGIN_ARCHITECTURE_ISSUES.md
5. **Test:** Run tests after each issue
6. **Document:** Update documentation as you implement

## Version History

- **v1.0** - Initial documentation created
- Contains 18 detailed issues for complete plugin architecture implementation
- Organized in 8 phases from core infrastructure to advanced features
- Includes backward compatibility requirements and migration path

---

**Remember:** The goal is incremental, safe refactoring that adds powerful extensibility while maintaining 100% backward compatibility. Take it one issue at a time, test thoroughly, and document as you go.
