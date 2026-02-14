#!/usr/bin/env bash

# Skill: Publish New Version
# Updates all pom.xml files with a new version (SemVer), validates with Maven,
# and suggests next steps (commit, tag, push).
# English only. Supports major, minor (default), patch bumps, and custom versions.

set -e

WORKSPACE_ROOT="${1:-.}"
cd "$WORKSPACE_ROOT" || exit 1

# Colors for output
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Find all pom.xml files recursively
find_pom_files() {
  find . -name "pom.xml" -type f | sort
}

# Parse version string and return major.minor.patch
parse_version() {
  local version="$1"
  if ! [[ "$version" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
    echo "ERROR: Invalid version format: $version" >&2
    exit 1
  fi
  echo "$version"
}

# Extract current version from root pom.xml
get_current_version() {
  local root_pom="$1"
  if [[ ! -f "$root_pom" ]]; then
    echo "ERROR: Root pom.xml not found at $root_pom" >&2
    exit 1
  fi
  grep -m1 "<version>" "$root_pom" | sed -E 's/.*<version>([0-9]+\.[0-9]+\.[0-9]+)<\/version>.*/\1/'
}

# Compute next version based on bump type or custom override
compute_next_version() {
  local current="$1"
  local bump="$2"
  local custom="$3"

  if [[ -n "$custom" ]]; then
    parse_version "$custom"
    return 0
  fi

  local major minor patch
  major=$(echo "$current" | cut -d. -f1)
  minor=$(echo "$current" | cut -d. -f2)
  patch=$(echo "$current" | cut -d. -f3)

  case "$bump" in
    major)
      echo "$((major + 1)).0.0"
      ;;
    minor)
      echo "$major.$((minor + 1)).0"
      ;;
    patch)
      echo "$major.$minor.$((patch + 1))"
      ;;
    *)
      echo "ERROR: Unknown bump type: $bump" >&2
      exit 1
      ;;
  esac
}

# Update <version>...</version> in a pom.xml file
update_pom_version() {
  local pom_file="$1"
  local new_version="$2"
  sed -E -i.bak "s/<version>[0-9]+\.[0-9]+\.[0-9]+<\/version>/<version>${new_version}<\/version>/" "$pom_file"
  rm -f "$pom_file.bak"
}

# Get current git branch
get_current_branch() {
  git rev-parse --abbrev-ref HEAD 2>/dev/null || echo ""
}

# Validate Maven project
validate_maven() {
  ./mvnw validate -q 2>/dev/null
}

restore_pom_files() {
  local pom_list="$1"

  if [[ -z "$pom_list" ]]; then
    echo -e "${YELLOW}No pom.xml files to restore.${NC}"
    return 0
  fi

  local restore_failed=0

  while IFS= read -r pom; do
    # Skip empty lines
    if [[ -z "$pom" ]]; then
      continue
    fi

    if git restore -q -- "$pom" 2>/dev/null; then
      echo -e "${GREEN}Restored ${pom} using git restore.${NC}"
    else
      echo -e "${YELLOW}git restore failed for ${pom}, falling back to git checkout...${NC}"
      if git checkout -- "$pom"; then
        echo -e "${GREEN}Restored ${pom} using git checkout.${NC}"
      else
        echo -e "${RED}Failed to restore ${pom}.${NC}" >&2
        restore_failed=1
      fi
    fi
  done <<< "$pom_list"

  return "$restore_failed"
}

run_git_steps() {
  local new_version="$1"
  local tag="v$new_version"
  git add .
  git commit -m "Bump version to $new_version"
  git tag "$tag"
  git push && git push --tags
}

# Main logic
main() {
  local root_pom="pom.xml"
  local current_version
  current_version=$(get_current_version "$root_pom")

  echo "Current version: $current_version"
  echo ""
  echo "Select version bump type:"
  echo "  1) minor (default)"
  echo "  2) major"
  echo "  3) patch"
  echo "  4) custom"
  read -p "Enter choice [1]: " choice
  choice=${choice:-1}

  local bump="minor"
  local custom=""

  case "$choice" in
    1) bump="minor" ;;
    2) bump="major" ;;
    3) bump="patch" ;;
    4)
      read -p "Enter custom version (e.g. 2.0.0): " custom
      custom=$(parse_version "$custom")
      ;;
    *)
      echo -e "${RED}Invalid choice. Aborting.${NC}"
      exit 1
      ;;
  esac

  local new_version
  new_version=$(compute_next_version "$current_version" "$bump" "$custom")

  echo ""
  echo "New version will be: $new_version"
  echo ""

  # Warn if not on main/master
  local branch
  branch=$(get_current_branch)
  if [[ "$branch" != "main" && "$branch" != "master" ]]; then
    echo -e "${YELLOW}WARNING: You are on branch '$branch'. It is recommended to run this skill on 'main' or 'master'.${NC}"
    echo ""
  fi

  # Confirm
  read -p "Proceed to update all pom.xml files to version $new_version? (y/N): " confirm
  if ! [[ "$confirm" =~ ^[Yy][Ee]?$ ]]; then
    echo "Aborted by user."
    exit 0
  fi

  # Update all pom.xml files
  echo ""
  echo "Updating pom.xml files..."
  local pom_files
  pom_files=$(find_pom_files)
  while IFS= read -r pom; do
    update_pom_version "$pom" "$new_version"
    echo "  Updated: $pom"
  done <<< "$pom_files"

  # Validate Maven
  echo ""
  echo "Validating Maven project..."
  if ! validate_maven; then
    echo -e "${RED}Maven validation failed. Please check for version mismatches or errors.${NC}"
    echo "Restoring pom.xml changes..."
    restore_pom_files "$pom_files"
    exit 1
  fi
  echo -e "${GREEN}Maven validation succeeded.${NC}"

  echo ""
  echo "Please review the changes now (e.g. git status, git diff)."
  read -p "Proceed with git add/commit/tag/push? (y/N): " proceed
  if ! [[ "$proceed" =~ ^[Yy][Ee]?$ ]]; then
    echo "Cancelled by user. Version changes preserved."
    echo "You can commit them manually or run 'git restore -- \$(find . -name pom.xml)' to revert."
    exit 0
  fi

  run_git_steps "$new_version"
  echo ""
  echo "Done, published version $new_version. Please verify the release on GitHub: https://github.com/auspis/fluent-sql-4j/actions"
}

main "$@"
