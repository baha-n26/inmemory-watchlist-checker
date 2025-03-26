package benchmark

import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import watchlist.CsvSanctionedPersonParser
import watchlist.SanctionedPerson
import watchlist.SanctionedPersonChecker
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.Level.Trial
import org.openjdk.jmh.annotations.Mode
import watchlist.SanctionedCandidateFetcher

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
open class SanctionedPersonUsageBenchmark {

    private lateinit var checker: SanctionedPersonChecker

    private lateinit var parser: CsvSanctionedPersonParser

    private lateinit var candidateFetcher: SanctionedCandidateFetcher

    @Setup(Trial)
    fun setup() {
        val parser = CsvSanctionedPersonParser(
            resourcePath = "/consolidated-screening-list.csv",
            fullNameColumn = 5,
            aliasesColumn = 22,
            fieldsDelimiter = ',',
            aliasesDelimiter = ";",
            filterColumnAndValue = 3 to "Individual",
        )
        candidateFetcher = SanctionedCandidateFetcher(parser.parse())
        checker = SanctionedPersonChecker(candidateFetcher)
    }

    @Benchmark
    fun checkPerson(): List<*> {
        return checker.check("John Doe")
    }

    @Benchmark
    fun addPerson() {
        candidateFetcher.addSanctionedPerson(SanctionedPerson("Jane Smith", listOf("J. Smith")))
    }

    @Benchmark
    fun createLuceneIndex(){
        SanctionedCandidateFetcher(parser.parse())
    }
}
