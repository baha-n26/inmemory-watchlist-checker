package watchlist

import org.apache.commons.text.similarity.JaroWinklerSimilarity
import kotlin.math.max

class SimilarityScorer(
    private val primarySimilarityWeight: Float = 0.8f,
    private val secondarySimilarityWeight: Float = 0.2f,
) {
    private val similarity = JaroWinklerSimilarity()

    fun score(input: String, primaryTarget: String, secondaryTargets: List<String>): Float {
        val normalizedInput = normalize(input)
        val primary = normalize(primaryTarget)
        val secondary = secondaryTargets.map(::normalize)

        val primaryOverlap = sharedTokenRatio(normalizedInput, primary)
        val bestSecondaryOverlap = secondary.maxOfOrNull { sharedTokenRatio(normalizedInput, it) } ?: 0.0f

        val primaryScore = similarity.apply(normalizedInput.sort(), primary.sort()).toFloat()
        val bestSecondaryScore = secondary.maxOfOrNull {
            similarity.apply(normalizedInput.sort(), it.sort()).toFloat()
        } ?: 0.0f

        val base = max(primaryScore, bestSecondaryScore)
        val tokenBoost = max(primaryOverlap, bestSecondaryOverlap)

        return primarySimilarityWeight * base + secondarySimilarityWeight * tokenBoost
    }

    private fun normalize(text: String): String = text.lowercase().replace(Regex("\\s+"), " ").trim()

    private fun sharedTokenRatio(s1: String, s2: String): Float {
        val tokens1 = s1.split(" ").toSet()
        val tokens2 = s2.split(" ").toSet()
        return tokens1.intersect(tokens2).size.toFloat() / max(tokens1.size, tokens2.size)
    }

    private fun String.sort(): String = split(" ").sorted().joinToString(" ")
}
