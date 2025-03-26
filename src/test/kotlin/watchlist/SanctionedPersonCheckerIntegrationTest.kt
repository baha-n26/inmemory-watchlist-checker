package watchlist

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SanctionedPersonCheckerIntegrationTest {

    private val sanctionedPersons = listOf(
        SanctionedPerson("John Doe", listOf("J. Doe", "Jon Doe")),
        SanctionedPerson("John Michael Doe", listOf("J. M. Doe", "John M. Doe", "J.M.D.", "Johnny Doe")),
        SanctionedPerson("Jonathan M. Doe", listOf("Jon M. Doe", "J. M. Doe", "J.D.", "Johnathan Doe")),
        SanctionedPerson("Jane Marie Doe", listOf("J. M. Doe", "Jane M. Doe", "Janie")),
        SanctionedPerson("João Miguel Dó", listOf("Joao M. Do", "J. M. Dó")),
    )

    private val sanctionedCandidateFetcher = SanctionedCandidateFetcher(sanctionedPersons.asSequence())

    private val sanctionedPersonChecker = SanctionedPersonChecker(sanctionedCandidateFetcher)

    @Test
    fun `should check a match for a full name exactly`() {
        val result = sanctionedPersonChecker.check("John Michael Doe")

        result shouldBe listOf(
            SanctionedPersonCheck("John Michael Doe", listOf("J. M. Doe", "John M. Doe", "J.M.D.", "Johnny Doe"), 1.0F)
        )
    }

    @Test
    fun `should check a fuzzy match on middle name missing`() {
        val result = sanctionedPersonChecker.check("John Doe")

        result.map { it.fullName } shouldContainAll listOf("John Doe", "John Michael Doe")
    }

    @Test
    @Disabled
    fun `should check a match accented name variation`() {
        val result = sanctionedPersonChecker.check("Joao Miguel Do")

        result.map { it.fullName } shouldContain "João Miguel Dó"
    }

    @Test
    fun `should check a non matching sanctioned name`() {
        val result = sanctionedPersonChecker.check("John Smith")

        result shouldBe emptyList()
    }

    @Test
    fun `should check a match name with name in different order`() {
        val result = sanctionedPersonChecker.check("Doe John Michael")

        result.map { it.fullName } shouldContain "John Michael Doe"
    }

    @Test
    fun `should match by alias only`() {
        val result = sanctionedPersonChecker.check("Johnny Doe")

        result.map { it.fullName } shouldContain "John Michael Doe"
    }

    @Test
    fun `should return multiple relevant matches`() {
        val result = sanctionedPersonChecker.check("J. M. Doe")

        val expectedNames = listOf("John Michael Doe", "Jonathan M. Doe", "Jane Marie Doe")
        result.map { it.fullName } shouldContainAll expectedNames
    }

    @Test
    fun `should ignore case and whitespace differences`() {
        val result = sanctionedPersonChecker.check("  john   michael DOE ")

        result.map { it.fullName } shouldContain "John Michael Doe"
    }

    @Test
    fun `should match diacritic-insensitive name`() {
        val result = sanctionedPersonChecker.check("Joao M. Do")

        result.map { it.fullName } shouldContain "João Miguel Dó"
    }

    @Test
    fun `should check a name as sanctioned that has been added to the list later on`() {
        sanctionedCandidateFetcher.addSanctionedPerson(
            SanctionedPerson("John Smith", listOf("J. Smith", "Jon Smith", "Jonathan Smith"))
        )

        val result = sanctionedPersonChecker.check("John Smith")

        result.map { it.fullName } shouldContain "John Smith"
    }
}
