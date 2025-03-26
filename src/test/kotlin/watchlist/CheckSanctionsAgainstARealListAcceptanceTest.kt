package watchlist

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class CheckSanctionsAgainstARealListAcceptanceTest {

    @Test
    fun `should check a potential full name match against the Consolidated Screening List of US Dept of Commerce`() {
        val csvParser = CsvSanctionedPersonParser(
            resourcePath = "/consolidated-screening-list.csv",
            fullNameColumn = 5,
            aliasesColumn = 22,
            fieldsDelimiter = ',',
            aliasesDelimiter = ";",
            filterColumnAndValue = 3 to "Individual",
        )
        val sanctionedCandidateFetcher = SanctionedCandidateFetcher(csvParser.parse())
        val sanctionedPersonChecker = SanctionedPersonChecker(sanctionedCandidateFetcher)

        val result = sanctionedPersonChecker.check("Marwan Mohammed ABU RAS")

        result shouldBe listOf(
            SanctionedPersonCheck(
                fullName = "Marwan Mohammed ABU RAS",
                aliases = listOf("Merwan Muhammed ABOU RAAS"),
                score = 1.0F
            )
        )
    }
}