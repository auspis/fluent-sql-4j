# Release 1.2.0

Release date: 2026-03-15

Highlights
- Minor release: bump to 1.2.0

Commits since v1.1.3
- 0a3fb7c4 chore(release): 1.2.0

Notes
Refactored pagination (Fetch) to use Long instead of Integer, updating the MySQL fetch rendering strategy and propagating the type change through the core DSL and tests.

Changes:

        Update Fetch clause fields from Integer to Long.
        Update DSL entrypoints (fetch/offset) and MySQL fetch PS strategy to use long/Long.
        Update affected unit/integration/E2E tests to construct Fetch with Long values.

```
git log --pretty=format:"%h %s" v1.1.3..v1.2.0
```

