package watchlist

class SanctionedPersonChecker(
    private val sanctionedCandidateFetcher: SanctionedCandidateFetcher,
    private val scorer: SimilarityScorer = SimilarityScorer(),
    private val scoreThreshold: Float = 0.9f
) {
    fun check(fullName: String): List<SanctionedPersonCheck> {
        val topCandidates = sanctionedCandidateFetcher.fetchTopCandidates(fullName, 20)
        val scoredResults = topCandidates.map {
            SanctionedPersonCheck(it.fullName, it.aliases, scorer.score(fullName, it.fullName, it.aliases))
        }
        return scoredResults.filter { it.score >= scoreThreshold }
    }
}

data class SanctionedPersonCheck(val fullName: String, val aliases: List<String>, val score: Float)
