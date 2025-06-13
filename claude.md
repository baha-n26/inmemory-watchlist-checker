# Claude PR Assistant

This repository integrates the Anthropics Claude model (via AWS Bedrock) to provide both manual and automatic code-review assistance on pull requests and issues.

---

## 1. Overview

The **Claude PR Assistant** GitHub Action will:

- **Automatically review** every pull request on open and on update, posting structured feedback directly as a PR review comment.
- **Respond to manual triggers** when you tag a comment or issue with `@claude`, running Claude on-demand for deeper insight or one-off analysis.

Under the hood, we leverage the [`anthropics/claude-code-action@beta`](https://github.com/anthropics/claude-code-action) runner with AWS Bedrock. We supply our Bedrock model ARN and AWS credentials as GitHub Secrets.

---

## 2. Prerequisites

Before enabling the workflow, confirm:

1. **AWS Bedrock Access**: Your AWS account must have an active Bedrock endpoint with a Claude-compatible inference profile.
2. **GitHub Secrets**: In your repository’s **Settings → Secrets**, define:
    - `AWS_ACCESS_KEY_ID`
    - `AWS_SECRET_ACCESS_KEY`
    - `AWS_REGION` (e.g. `eu-central-1`)
    - We are currently using static AWS credentials for Bedrock.

---

## 3. Workflow Configuration

The workflow configuration lives in **`.github/workflows/claude.yml`**, and defines two jobs:

- **Manual Trigger (`claude-manual`)**: Responds whenever a comment or review contains `@claude`.
- **Automatic Review (`claude-auto-review`)**: Runs on every pull-request open or update.

To customize behavior, edit the inputs for each job in that file:

- **`trigger_phrase`**, **`timeout_minutes`**, or **`model`** under `claude-manual`.
- **`direct_prompt`** under `claude-auto-review`, which controls how Claude structures its feedback.

For the full YAML and any additional settings, see the actual workflow file in **`.github/workflows/claude.yml`**.

### Example: Tailoring the `direct_prompt`

```yaml
direct_prompt: |
  # Automated Code Review for Kotlin API v2.1

  ## 1. PR Intent & Scope
  • Describe the purpose of this PR and any new modules or refactors.

  ## 2. Architecture & DI
  • Verify alignment with our hexagonal / layered architecture.
  • Ensure dependency injection (Koin) is used correctly.

  ## 3. Endpoints & Contracts
  • Identify any breaking changes to public REST endpoints.
  • Check OpenAPI annotations and HTTP status codes.
```