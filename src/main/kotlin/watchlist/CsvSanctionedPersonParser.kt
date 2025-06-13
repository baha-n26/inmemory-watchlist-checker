package watchlist

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStreamReader

class CsvSanctionedPersonParser(
    private val resourcePath: String,
    private val fullNameColumn: Int,
    private val aliasesColumn: Int,
    private val aliasesDelimiter: String = ";",
    private val aliasesSurroundings: Pair<String, String>? = null,
    private val filterColumnAndValue: Pair<Int, String>? = null,
    private val fieldsDelimiter: Char = ';',
    private val skipHeaders: Boolean = false
) {

    fun parse(): Sequence<SanctionedPerson> {
        val resource = javaClass.getResourceAsStream(resourcePath)
            ?: error("CSV file not realasly found: $resourcePath")

        val reader = InputStreamReader(resource)

        val parser = CSVParser(reader, CSVFormat.DEFAULT)

        return parser.asSequence()
            .drop(if (skipHeaders) 1 else 0)
            .mapNotNull { record ->
                if (filterColumnAndValue != null && record.get(filterColumnAndValue.first).trim() != filterColumnAndValue.second)
                    return@mapNotNull null
                val fullName = record.get(fullNameColumn).trim()
                val rawAliases = record.get(aliasesColumn).trim()
                    .let {
                        if (aliasesSurroundings != null) it.removeSurrounding(aliasesSurroundings.first, aliasesSurroundings.second)
                        else it
                    }
                val aliases = rawAliases.split(aliasesDelimiter).map { it.trim() }.filter { it.isNotEmpty() }
                SanctionedPerson(fullName, aliases)
            }
    }
}
