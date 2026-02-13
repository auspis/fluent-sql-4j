**Publish to Maven Central**

As of June 30, 2025, OSSRH (Sonatype Open Source Repository Hosting) has reached end-of-life. This project now publishes via the **Central Publishing Portal** (https://central.sonatype.com/).

## ARM64 Architecture Support

This project supports building and publishing on both x64 (Intel/AMD) and ARM64 architectures. The release workflow can run on either architecture to ensure compatibility and optimal performance.

**Note**: Java artifacts are platform-independent (bytecode is the same regardless of build architecture). The architecture selection primarily affects:
- Build and test performance (ARM64 may be faster on ARM-based CI runners)
- Integration test execution (ensures compatibility on both architectures)
- Native dependencies in Testcontainers (database containers)

## Setup

### 1. Generate a Central Portal User Token

1. Log in to [Central Portal](https://central.sonatype.com/) with your OSSRH credentials (same account)
2. Go to "View Account" → "Generate User Token"
3. Save the token username and password

### 2. Set Repository Secrets in GitHub

Set these secrets in *Settings → Secrets and variables → Actions*:

- `CENTRAL_TOKEN_USERNAME` - User token username from step 1
- `CENTRAL_TOKEN_PASSWORD` - User token password from step 1
- `GPG_PRIVATE_KEY` (ASCII-armored) (use `data/scripts/generate-gpg-secrets.sh`)
- `GPG_PASSPHRASE` (if applicable) (see `data/scripts/generate-gpg-secrets.sh`)

## Release Workflow

### Default Behavior: Manual Publishing (Conservative)

By default, the release workflow uploads and validates artifacts without automatically publishing them. This allows for review before they go public to Maven Central.

**Option 1: Push a tag (automatic trigger, manual review, default x64 architecture)**

```bash
git tag v1.0.0
git push origin v1.0.0
```

The GitHub Actions workflow will automatically:
1. Build the release artifacts on x64 architecture (default)
2. Sign them with GPG
3. Upload to Central Publishing Portal
4. Validate the bundle
5. **Stop** (waiting for manual publishing)
6. You review at https://central.sonatype.com/publishing/deployments
7. You click "Publish" to make them live on Maven Central

**Option 2: Manual workflow dispatch (manual review, choose architecture)**

Go to GitHub → Actions → "Release to Maven Central" → "Run workflow":
- Set `releaseTag` (e.g., v1.0.0)
- Set `autoPublish = false`
- Set `architecture = x64` or `arm64` (default: x64)
- Click "Run workflow"

This gives you control over:
- Which architecture to build on (x64 or arm64)
- Manual review before publishing (autoPublish = false)

### Auto-Publishing to Maven Central

**Option 3: Auto-publish via workflow dispatch (choose architecture)**

Go to GitHub → Actions → "Release to Maven Central" → "Run workflow":
- Set `releaseTag` (e.g., v1.0.0)
- Set `autoPublish = true`
- Set `architecture = x64` or `arm64` (default: x64)
- Click "Run workflow"

This will:
1. Build and upload artifacts on the selected architecture
2. Validate the bundle
3. Automatically publish to Maven Central (no manual review)
4. Artifacts appear on Maven Central after ~10 minutes

### Architecture Selection Guide

- **x64 (Intel/AMD)**: Default, widely compatible, standard GitHub-hosted runners
- **arm64 (ARM64)**: Use if you want to build on ARM architecture for:
  - Performance benefits on ARM-based CI infrastructure
  - Testing ARM compatibility
  - Verifying builds work correctly on ARM processors

**Important**: Java artifacts are platform-independent, so the choice of build architecture doesn't affect the published artifacts themselves. Both architectures produce identical bytecode.

---

## How the Release Workflow Works

The workflow is configured with two trigger modes:

1. **Tag push** (`git push` with a tag matching `v*.*.*`):
   - Automatic trigger (no manual action needed)
   - Uses `autoPublish = false` (default)
   - Uses `x64` architecture (default)
   - Artifacts are uploaded and validated, waiting for manual publishing
2. **Manual dispatch** (GitHub Actions UI):
   - Manual trigger via "Run workflow" button
   - User chooses `autoPublish = true` or `false`
   - User chooses `architecture = x64` or `arm64`
   - Control over manual vs. automatic publishing and build architecture

The Maven command parameter `-DautoPublish` controls the Central Publishing Plugin behavior:
- `false`: Artifacts uploaded and validated; you publish manually via https://central.sonatype.com/publishing/deployments (conservative, recommended)
- `true`: Artifacts uploaded, validated, and automatically published (immediate release)

The workflow `architecture` parameter controls which GitHub runner is used:
- `x64`: Uses standard `ubuntu-latest` runners (Intel/AMD 64-bit)
- `arm64`: Uses `ubuntu-24.04-arm64` runners (ARM64 architecture)

---

Optional: run a build/verify in CI before creating the release tag.

**Artifacts published by the release**

The release publishes these modules and attached artifacts:

- `api`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `spi`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `test-support`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `core`: main JAR, `-sources.jar`, `-javadoc.jar` (generated with Javadoc warnings due to Lombok-generated classes), module POM, and GPG signature (`.asc`).
- `plugin-mysql`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).
- `plugin-postgresql`: main JAR, `-sources.jar`, `-javadoc.jar`, module POM, and GPG signature (`.asc`).

Note: `core` is built and published but does not skip deployment. The Javadoc generation for `core` uses `doclint=none` and `failOnError=false` to handle Lombok-generated inner classes that cannot be resolved by Javadoc, resulting in warnings but a valid Javadoc JAR.

**Why `packaging = pom` modules publish only a POM**

Modules with `packaging = pom` (for example the root project and the `plugins` parent) do not produce runtime JARs. Maven publishes their POM files only. These parent/aggregator POMs carry `dependencyManagement`, `pluginManagement` and module lists — publishing the POM exposes that metadata to consumers but there is no `-<version>.jar` for such modules.

You can verify this locally:

```bash
# Build everything that would be attached by the release (no deploy)
./mvnw -P release -DskipTests clean package

# Or build and install to the local repo to verify consumer resolution
./mvnw -P release -DskipTests clean install

# Check module target artifacts (replace '1.0' with the actual version if different)
ls -l api/target/api-*.jar api/target/api-*-sources.jar api/target/api-*-javadoc.jar
ls -l spi/target/spi-*.jar spi/target/spi-*-sources.jar spi/target/spi-*-javadoc.jar
ls -l test-support/target/test-support-*.jar
ls -l plugins/plugin-mysql/target/plugin-mysql-*.jar plugins/plugin-postgresql/target/plugin-postgresql-*.jar

# After 'install' verify local repository coordinates
ls -l ~/.m2/repository/io/github/auspis/fluentsql4j/api/* ~/.m2/repository/io/github/auspis/fluentsql4j/spi/*

# Verify packaging=pom modules produce a POM and no JAR
ls -l plugins/target/plugins-*.pom
test -f plugins/target/plugins-1.0.pom && echo "plugins POM present" || echo "plugins POM missing"
test -f plugins/target/plugins-1.0.jar && echo "unexpected JAR for plugins" || echo "no JAR for plugins (expected)"
```

**Notes**

- The CI release workflow config in `.github/workflows/release.yml` runs `mvn -B -DskipTests -DautoPublish=<value> clean deploy -P release` and uses `actions/setup-java` to configure Central Portal credentials and GPG signing.
- Source and Javadoc jars are attached via the source/javadoc plugins configured in the parent POM. GPG signatures (`.asc`) are produced during the verify phase when signing is enabled (CI supplies the GPG key).
- By default, `autoPublish=false`, which provides a conservative manual-review approach.
- **OSSRH has been sunset as of June 30, 2025.** This project now uses the Central Publishing Portal exclusively.

**References**

- [Central Publishing Portal Documentation](https://central.sonatype.org/)
- [Generate Portal User Token](https://central.sonatype.org/publish/generate-portal-token/)
- [Maven Publishing Guide](https://central.sonatype.org/publish/publish-portal-maven/)
- [OSSRH EOL Migration Guide](https://central.sonatype.org/pages/ossrh-eol/)

