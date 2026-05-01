This module contains tests that require concrete runtime dependencies we do not want in the production module graph or published artifacts.

It exists to keep the production JPMS graph and Maven artifacts clean, while still allowing realistic tests to run consistently in both Maven and IDEs.

Use this module only for tests whose realism requires concrete runtime dependencies intentionally excluded from production modules.
