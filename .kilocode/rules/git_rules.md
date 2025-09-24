### Commit instructions
- **Title**
  - Start with a gitmoji emoji in unicode (e.g. ✨ for feature).
  - Max 50 characters.
  - Format: `✨ short keywords`
- **Body**
  - Leave one blank line after the title.
  - Use - per line for each change.
  - Each line = concrete action (e.g. - add user model).
  - Use simple English, action verbs.
  - No long sentences, no filler.

### Creating a Pull request
- Retreive all commits involved in the pull request
- Fill the following template based on the commits and the changes :

```md
## ✅ Type of PR

<!--
Instructions:
  - At least one of these must be checked.
  - If you do not know which one to pick, pick "Feature".
  - Base yourself on commit history.
-->

- [ ] Refactor
- [ ] Feature
- [ ] Bug Fix
- [ ] Optimization
- [ ] Documentation Update

## 🗒️ Description

<!--
Instructions:
- make the most concise description possible.
- focus on functional changes, not technical details.
- put important details here if necessary, in **bold**.
-->

## 🚶‍➡️ Behavior

<!--
Instructions:
- Summarize the behavior changes in a few sentences.
- Use bullet points.
- Focus on the user experience.
- Be precise, not vague.
-->

## 🧪 Steps to test

<!--
Instructions:
- Give a list of steps to test the PR, using UI or not.
- Use checkboxes.
- Do not include installation process, focus on functional testing.
```