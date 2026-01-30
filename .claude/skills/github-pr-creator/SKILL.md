---
name: github-pr-creator
description: Create GitHub pull requests using a custom template with auto-generated content. Use when the user asks to create a PR, open a pull request, or submit changes for review. The skill analyzes commit history, generates appropriate PR content (type, description, behavior, test steps), handles branch pushing, and supports labels and milestones.
---

# GitHub PR Creator

Create GitHub pull requests using a custom template with auto-generated content based on commit analysis.

## Prerequisites

- GitHub CLI (`gh`) must be installed and authenticated
- Must be in a git repository with commits to submit
- Remote repository must be configured

## Quick Start

When the user asks to create a PR, follow this workflow:

1. **Verify branch status**
   - Check current branch and ensure commits exist
   - Verify or push branch to remote if needed

2. **Analyze commits**
   - Run `git log <base-branch>..<current-branch>` to see all commits
   - Run `git diff --stat <base-branch>...<current-branch>` for change summary
   - Run `git diff <base-branch>...HEAD` for detailed changes

3. **Generate PR content**
   - Analyze commit messages to determine PR type (Refactor, Feature, Bug Fix, etc.)
   - Extract meaningful description from commit messages
   - Summarize behavior changes focusing on user impact
   - Create actionable test steps

4. **Create PR with template**
   - Read the template from `assets/pr-template.md`
   - Replace these placeholders with generated content:
     - `{{TYPE_CHECKBOXES}}` - Checkboxes with auto-detected types marked
     - `{{DESCRIPTION}}` - Concise functional description
     - `{{BEHAVIOR}}` - User-facing behavior changes (bullet points)
     - `{{TEST_STEPS}}` - Testing checklist items
   - Use `gh pr create` with the filled template

## Template Placeholders

### {{TYPE_CHECKBOXES}}

Generate markdown checkboxes based on commit analysis:

```markdown
- [x] Feature
- [ ] Bug Fix
- [ ] Refactor
- [ ] Optimization
- [ ] Documentation Update
```

Detection rules:
- Check commits containing "feat", "feature", "add" → Feature
- Check commits containing "fix", "bug" → Bug Fix
- Check commits containing "refactor" → Refactor
- Check commits containing "doc", "docs", "documentation" → Documentation

### {{DESCRIPTION}}

Concise functional description (2-3 sentences). Focus on WHAT changed, not HOW:

- Extract key information from commit messages
- Summarize main functionality added or modified
- Highlight important details in **bold**
- Avoid technical implementation details

### {{BEHAVIOR}}

User-facing behavior changes as bullet points:

- Focus on user experience and observable changes
- Be specific and precise
- Use action-oriented language
- Example: "- Users can now filter tasks by priority"

### {{TEST_STEPS}}

Testing checklist using GitHub checkboxes (`- [ ]`):

```markdown
- [ ] Verify task creation with all fields
- [ ] Test priority filtering on task list
- [ ] Check validation error messages
```

Include functional tests relevant to the changes.

## Command Examples

### Basic PR creation

```bash
gh pr create \
  --title "Add task priority filtering" \
  --base main \
  --body "$(cat filled-template.md)"
```

### With labels and milestone

```bash
gh pr create \
  --title "Add task priority filtering" \
  --base main \
  --body "$(cat filled-template.md)" \
  --label "feature,frontend" \
  --milestone "v2.0"
```

### Using heredoc for body

```bash
gh pr create --title "PR Title" --base main --body "$(cat <<'EOF'
## ✅ Type of PR
- [x] Feature
...
EOF
)"
```

## Workflow Tips

- **Always analyze commits first** before generating content
- **Check if branch is pushed** - auto-push if not
- **Be concise** - template instructions emphasize brevity
- **Focus on user impact** - behavior section is about UX, not code
- **Make tests actionable** - specific steps, not vague "test the feature"
- **Use commit history** - it reveals what type of PR this is

## Error Handling

- If no commits between branches: Inform user, cannot create PR
- If `gh` not installed: Instruct to install GitHub CLI
- If not authenticated: Run `gh auth login`
- If branch not pushed: Auto-push with `git push -u origin <branch>`
- If template file missing: Check `assets/pr-template.md` exists

## Auto-push Logic

Before creating PR:

```bash
# Check if branch exists on remote
if ! git ls-remote --heads origin "$BRANCH" | grep -q "$BRANCH"; then
  git push -u origin "$BRANCH"
fi

# Check if local is ahead
if [ "$(git rev-parse @)" != "$(git rev-parse @{u})" ]; then
  git push
fi
```

## Complete Workflow Example

User asks: "Create a PR for my task filtering feature"

1. Verify setup:
   ```bash
   git branch --show-current
   git status
   ```

2. Analyze commits:
   ```bash
   git log main..current-branch --oneline
   git diff --stat main...current-branch
   git diff main...HEAD
   ```

3. Generate content:
   - Type: Feature (commits mention "add filtering")
   - Description: "Add priority-based filtering to task list. Users can filter tasks by HIGH, MEDIUM, or LOW priority using dropdown in UI."
   - Behavior:
     - Users can filter tasks by selecting priority from dropdown
     - Filter persists across page refreshes via localStorage
     - "Clear filters" button resets all active filters
   - Test steps:
     - [ ] Select HIGH priority filter and verify only high-priority tasks show
     - [ ] Refresh page and verify filter persists
     - [ ] Click "Clear filters" and verify all tasks display

4. Fill template and create PR:
   ```bash
   gh pr create \
     --title "Add task priority filtering" \
     --base main \
     --body "<filled template content>" \
     --label "feature,frontend"
   ```

## Notes

- Template file is located at `assets/pr-template.md`
- Template is designed for this specific repository's conventions
- Placeholder format is `{{PLACEHOLDER_NAME}}`
- All placeholders must be replaced before creating PR
- The skill automatically detects PR type from commit messages
