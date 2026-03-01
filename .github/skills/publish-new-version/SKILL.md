---

name: publish-new-version
description: Automates Maven multi-module version bumping following SemVer (major, minor, patch, or custom). Updates all pom.xml files, validates with Maven, and guides through git commit/tag/push workflow. Use when releasing a new version, bumping version numbers, preparing Maven releases, or publishing to Maven Central.
license: MIT
compatibility: Requires Maven wrapper (./mvnw), git, bash, and a multi-module Maven project with pom.xml files
metadata:
  author: auspis
  version: 0.1.0
---

# Publish New Version

Automates the complete workflow for publishing a new version of a Maven multi-module project following Semantic Versioning (SemVer) conventions.

## Overview

This skill handles the entire version release process for Maven projects:

1. **Detects current version** from root `pom.xml` using Maven's help:evaluate
2. **Prompts for version bump type**: major, minor (default), patch, or custom
3. **Updates all pom.xml files** recursively (root + all modules)
4. **Validates** the updated project with `mvn validate`
5. **Guides through git workflow**: commit, tag, push (with user confirmation)
6. **Includes safety checks**: warns if not on main/master, allows rollback on validation failure

## When to Use This Skill

Use this skill when you need to:

- Release a new version of your Maven project
- Bump version numbers across all modules consistently
- Follow SemVer conventions (major.minor.patch)
- Prepare for publishing to Maven Central or other repositories
- Ensure version consistency across multi-module Maven projects
- Automate the release workflow (version → validate → commit → tag → push)

## Prerequisites

Before using this skill, ensure:

- **Maven wrapper** (`./mvnw`) exists and is executable in the workspace root
- **Git repository** is initialized and you have commits
- **Multi-module Maven project** with `pom.xml` files
- You are on the correct branch (preferably `main` or `master`)
- You have reviewed and finalized all changes for the release

## How It Works

### 1. Version Detection

The skill uses Maven's `help:evaluate` plugin to extract the current version from the root `pom.xml`:

```bash
./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout
```

This ensures accurate version detection even in complex multi-module setups.

### 2. Version Bump Types

Choose from four bump types following Semantic Versioning:

|    Type    |             Description             | Example (from 1.2.3) |
|:----------:|:------------------------------------|:--------------------:|
| **minor**  | Default. Adds new features          | 1.3.0                |
| **major**  | Breaking changes, major release     | 2.0.0                |
| **patch**  | Bug fixes, patches                  | 1.2.4                |
| **custom** | Specify exact version (e.g., 2.5.0) | 2.5.0                |

### 3. Update Process

The skill:

- Finds all `pom.xml` files recursively in the workspace
- Updates `<version>X.Y.Z</version>` tags using `sed`
- Validates the project with `./mvnw validate`
- **Rolls back changes** if validation fails

### 4. Git Workflow

After successful validation, the skill prompts to:

1. **Review changes**: Check with `git status` or `git diff`
2. **Stage all files**: `git add .`
3. **Commit**: `git commit -m "Bump version to X.Y.Z"`
4. **Tag**: `git tag vX.Y.Z`
5. **Push**: `git push && git push --tags`

You can cancel at any step to perform manual operations.

## Safety Features

- ⚠️ **Branch warning**: Alerts if you're not on `main` or `master`
- ✅ **Maven validation**: Ensures project compiles after version changes
- 🔄 **Automatic rollback**: Restores pom.xml files if validation fails using `git restore`
- 🛑 **User confirmation**: Requires explicit approval before git operations
- 📋 **Review step**: Allows manual inspection before committing

## Usage Example

When an agent invokes this skill:

```
Agent: "I want to publish a new minor version"

Skill Output:
> Current version: 1.2.3
> 
> Select version bump type:
>   1) minor (default)
>   2) major
>   3) patch
>   4) custom
> Enter choice [1]: 1
>
> New version will be: 1.3.0
>
> Proceed to update all pom.xml files to version 1.3.0? (y/N): y
>
> Updating pom.xml files...
>   Updated: ./pom.xml
>   Updated: ./core/pom.xml
>   Updated: ./api/pom.xml
>   ...
>
> Validating Maven project...
> Maven validation succeeded.
>
> Please review the changes now (e.g. git status, git diff).
> Proceed with git add/commit/tag/push? (y/N): y
>
> Done, published version 1.3.0. Please verify the release on GitHub.
```

## Error Handling

If validation fails:

```
Maven validation failed. Please check for version mismatches or errors.
Restoring pom.xml changes...
Restored ./pom.xml using git restore.
Restored ./core/pom.xml using git restore.
...
```

All changes are automatically reverted to prevent broken state.

## Script Location

The implementation is in [`scripts/publish-new-version.sh`](scripts/publish-new-version.sh).

## Next Steps After Publishing

After successful version publishing:

1. **Verify GitHub Actions**: Check that the CI/CD pipeline runs successfully
2. **Create GitHub Release**: Draft release notes on GitHub
3. **Deploy to Maven Central**: If configured, deploy artifacts (see repository docs)
4. **Update documentation**: Changelog, README badges, etc.
5. **Announce release**: Notify users, update release channels

## Troubleshooting

**Problem**: "Maven wrapper (./mvnw) not found or not executable"
- **Solution**: Ensure you're in the workspace root and `./mvnw` exists with execute permissions

**Problem**: "Maven validation failed"
- **Solution**: Check for version mismatches in dependency declarations or parent POM references

**Problem**: "You are on branch 'feature-xyz'"
- **Solution**: Switch to `main` or `master` branch before releasing, or proceed with caution

**Problem**: Git push fails (authentication)
- **Solution**: The skill doesn't handle git push failures automatically. Configure git credentials or SSH keys before running

## Related Documentation

For more details on the project release process, see:
- [`data/wiki/PUBLISH_TO_MAVEN_CENTRAL.md`](../../../data/wiki/PUBLISH_TO_MAVEN_CENTRAL.md) - Publishing workflow
- [`data/wiki/DEVELOPER_GUIDE.md`](../../../data/wiki/DEVELOPER_GUIDE.md) - Development guidelines
