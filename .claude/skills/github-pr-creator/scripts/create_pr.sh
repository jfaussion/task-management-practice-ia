#!/bin/bash
# GitHub PR Creator Script
# Creates a PR using the custom template with auto-generated content

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SKILL_DIR="$(dirname "$SCRIPT_DIR")"
TEMPLATE_FILE="$SKILL_DIR/assets/pr-template.md"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored messages
log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    log_error "GitHub CLI (gh) is not installed. Please install it first."
    exit 1
fi

# Get current branch
CURRENT_BRANCH=$(git branch --show-current)
if [ -z "$CURRENT_BRANCH" ]; then
    log_error "Not on a branch. Please checkout a branch first."
    exit 1
fi

log_info "Current branch: $CURRENT_BRANCH"

# Check if branch exists on remote, if not push it
if ! git ls-remote --heads origin "$CURRENT_BRANCH" | grep -q "$CURRENT_BRANCH"; then
    log_warn "Branch not found on remote. Pushing to origin..."
    git push -u origin "$CURRENT_BRANCH"
else
    # Check if local is ahead of remote
    LOCAL=$(git rev-parse @)
    REMOTE=$(git rev-parse @{u} 2>/dev/null || echo "")

    if [ -n "$REMOTE" ] && [ "$LOCAL" != "$REMOTE" ]; then
        log_warn "Local branch has unpushed commits. Pushing to origin..."
        git push
    fi
fi

# Get base branch (usually main or master)
BASE_BRANCH=${1:-$(git symbolic-ref refs/remotes/origin/HEAD | sed 's@^refs/remotes/origin/@@')}
log_info "Base branch: $BASE_BRANCH"

# Analyze commits
log_info "Analyzing commits..."
COMMIT_LOG=$(git log --oneline "$BASE_BRANCH..$CURRENT_BRANCH")
COMMIT_COUNT=$(echo "$COMMIT_LOG" | wc -l | tr -d ' ')

if [ "$COMMIT_COUNT" -eq 0 ]; then
    log_error "No commits found between $BASE_BRANCH and $CURRENT_BRANCH"
    exit 1
fi

log_info "Found $COMMIT_COUNT commit(s)"

# Get detailed diff
DIFF_STAT=$(git diff --stat "$BASE_BRANCH...$CURRENT_BRANCH")

# Read template
if [ ! -f "$TEMPLATE_FILE" ]; then
    log_error "Template file not found at: $TEMPLATE_FILE"
    exit 1
fi

TEMPLATE_CONTENT=$(cat "$TEMPLATE_FILE")

# Generate PR title from branch name or first commit
PR_TITLE=$(echo "$CURRENT_BRANCH" | sed 's/-/ /g' | sed 's/\b\(.\)/\u\1/g')

# Analyze commit messages to determine PR type
REFACTOR_COUNT=$(echo "$COMMIT_LOG" | grep -i "refactor" | wc -l | tr -d ' ')
FEATURE_COUNT=$(echo "$COMMIT_LOG" | grep -iE "feat|feature|add" | wc -l | tr -d ' ')
BUGFIX_COUNT=$(echo "$COMMIT_LOG" | grep -iE "fix|bug" | wc -l | tr -d ' ')
DOCS_COUNT=$(echo "$COMMIT_LOG" | grep -iE "doc|docs" | wc -l | tr -d ' ')

# Generate type checkboxes
TYPE_CHECKBOXES=""
TYPE_CHECKBOXES+="- [$([ "$REFACTOR_COUNT" -gt 0 ] && echo 'x' || echo ' ')] Refactor\n"
TYPE_CHECKBOXES+="- [$([ "$FEATURE_COUNT" -gt 0 ] && echo 'x' || echo ' ')] Feature\n"
TYPE_CHECKBOXES+="- [$([ "$BUGFIX_COUNT" -gt 0 ] && echo 'x' || echo ' ')] Bug Fix\n"
TYPE_CHECKBOXES+="- [ ] Optimization\n"
TYPE_CHECKBOXES+="- [$([ "$DOCS_COUNT" -gt 0 ] && echo 'x' || echo ' ')] Documentation Update"

# Generate description from commit messages
DESCRIPTION=$(echo "$COMMIT_LOG" | head -5 | sed 's/^[a-f0-9]* /- /')

# Generate behavior section
BEHAVIOR="- Changes based on $COMMIT_COUNT commit(s)\n"
BEHAVIOR+="- Files modified: $(git diff --name-only "$BASE_BRANCH...$CURRENT_BRANCH" | wc -l | tr -d ' ')"

# Generate test steps
TEST_STEPS="- [ ] Verify the changes work as expected\n"
TEST_STEPS+="- [ ] Check for any console errors\n"
TEST_STEPS+="- [ ] Test edge cases"

# Replace placeholders
PR_BODY="$TEMPLATE_CONTENT"
PR_BODY=$(echo "$PR_BODY" | sed "s|{{TYPE_CHECKBOXES}}|$TYPE_CHECKBOXES|")
PR_BODY=$(echo "$PR_BODY" | sed "s|{{DESCRIPTION}}|$DESCRIPTION|")
PR_BODY=$(echo "$PR_BODY" | sed "s|{{BEHAVIOR}}|$BEHAVIOR|")
PR_BODY=$(echo "$PR_BODY" | sed "s|{{TEST_STEPS}}|$TEST_STEPS|")

# Build gh pr create command
GH_CMD="gh pr create --title \"$PR_TITLE\" --base \"$BASE_BRANCH\" --body \"\$PR_BODY\""

# Add labels if provided
if [ -n "$LABELS" ]; then
    GH_CMD+=" --label \"$LABELS\""
fi

# Add milestone if provided
if [ -n "$MILESTONE" ]; then
    GH_CMD+=" --milestone \"$MILESTONE\""
fi

log_info "Creating PR..."
echo -e "\n$PR_BODY\n"

# Create the PR
eval "$GH_CMD"

log_info "PR created successfully!"
