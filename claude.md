# CLAUDE PR Assistant

This `CLAUDE.md` file defines the code-review standards, project rules, and metrics that guide **Claude** when evaluating pull requests for our **Kotlin API v2.1** service.

---

## 1. High-Level Project Summary

**Purpose:**  
A backend service that:
- **Ingests** a 1 GB+ CSV of millions of sanctioned/blacklisted person records via streaming to avoid Out of Memory Exceptions.
- **Builds** and maintains in-memory Lucene indexes for sub-500 ms lookups.
- **Provides** a Service (`SanctionedCandidateFetcher`) supporting name, DOB, and source filters for lookups against lucene Indexes.
- **Scores** Lucene returned hits via Jaro-Winkler similarity, filtering for hits only above the configured threshold.

**Core Functionality:**
- `src/main/kotlin/watchlist/CsvSanctionedPersonParser.kt` (Parsing the CSV file)
- `src/main/kotlin/watchlist/SanctionedCandidateFetcher.kt` (Searching against Lucene indexes)
- `src/main/kotlin/watchlist/SanctionedPersonChecker.kt` (Service integrating Lucene Search and Jaro Winkler Similarity Scoring to check if a person is sanctioned)
- `src/main/kotlin/watchlist/SimilarityScorer.kt` (Calculating Jaro-Winkler similarity scores)
- `src/jmh/kotlin/benchmark/` (JMH tests for performance benchmarks)

---

## 2. North-Star Metrics & Goals

| Metric                | Target                                  |
|-----------------------|-----------------------------------------|
| Search Latency        | p99 < 500 ms under 200 req/sec          |
| Memory Footprint      | Within configured JVM heap (max 2.4 GB) |
| Match Accuracy        | ≤ 1% false positives via similarity     |
| Test Coverage         | ≥ 90% on parser/index/similarity        |
| Benchmark Consistency | No regressions in `benchmark/` suite    |

These metrics are our “north stars.” Claude should flag PRs that risk regressions in any of these areas.

---

## 3. Audience & Usage

- **Product Managers:** High-level service overview, success criteria, and how PR reviews enforce them.
- **Developers:** Project-specific style guide, review checklist, and patterns to follow.
- **Maintainers:** Instructions for customizing triggers and prompts in `.github/workflows/claude.yml`.

## 5. Code Style & Conventions

1. **Kotlin Idioms:**
   - Use extension functions, data classes, sealed classes, null-safe operators.
2. **Formatting & Linting:**
   - Comply with `ktlint` and `detekt`; zero violations on `gradle ktlintCheck`.
3. **Concurrency:**
   - Avoid `runBlocking`; use structured concurrency (`Dispatchers.IO`, coroutines).

---

## 6. Review Criteria

Claude will validate each PR against these checkpoints:

1. **Architecture & Layering**
   - Clear separation of core functionality logic (parsing, indexing, searching, scoring).
2. **Performance & Scalability**
   - Benchmarks meet p95 latency and memory targets.
3. **Similarity & Accuracy**
   - Jaro-Winkler threshold values correct; edge-case tests present.
4. **Testing & Reliability**
   - Parser/index tests stream large files; integration tests simulate load.
5. **Observability & Monitoring**
   - Micrometer metrics for latency and index size; logs via `LoggingInterceptor`.
6. **Security & Data Handling**
   - Sanitize inputs to prevent Lucene query injection; secure config management.
7. **Documentation**
   - APIs and core functionality are documented; updates to `README.md` and `docs/adr/`.

---

## 7. Project-Specific Rules & Patterns

- **Lucene:** Use a single `Directory` instance; always close `IndexWriter`.
- **Benchmarks:** New code paths require corresponding testing and/or benchmarks.
- **Similarity Tests:** Cover diacritics, punctuation, casing.

---

## 8. Workflow Pointer

- **Workflow:**
   - `.github/workflows/claude.yml` contains `claude-manual` and `claude-auto-review` jobs.
   - The `direct_prompt` in the auto-review job references this `CLAUDE.md` context.

For full configuration details, review both this file and the [Anthropic Docs on GitHub Actions](https://docs.anthropic.com/en/docs/claude-code/github-actions).
