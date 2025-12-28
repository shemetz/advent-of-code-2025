import java.io.File

fun main() {
    val lines = File("src/input.txt").readLines()
    var lineIdx = 1
    var beamLocs = setOf(lines.first().indexOf('S'))
    var splitsCount = 0
    while (lineIdx < lines.size) {
        val line = lines[lineIdx]
        val newBeamLocs = (0..<line.length)
            .filter { idx ->
                when (line[idx]) {
                    '^' -> false
                    '.' -> idx in beamLocs
                            || (idx - 1 in beamLocs && line.getOrNull(idx - 1) == '^')
                            || (idx + 1 in beamLocs && line.getOrNull(idx + 1) == '^')

                    else -> throw IllegalArgumentException("Unknown character: ${line[idx]}")
                }
            }
            .toSet()
        splitsCount += beamLocs.count { line[it] == '^' }
        beamLocs = newBeamLocs
        lineIdx++
    }
    println("Part 1: $splitsCount")  // 1675

    lineIdx = 1
    var beamLocsAndCounts = mapOf(Pair(lines.first().indexOf('S'), 1L))
    while (lineIdx < lines.size) {
        val line = lines[lineIdx]
        val newBeamLocsAndCounts = (0..<line.length).associateWith { idx ->
            when (line[idx]) {
                '^' -> 0
                '.' -> {
                    var count = beamLocsAndCounts.getOrDefault(idx, 0)
                    if (line.getOrNull(idx - 1) == '^') {
                        count += beamLocsAndCounts.getOrDefault(idx - 1, 0)
                    }
                    if (line.getOrNull(idx + 1) == '^') {
                        count += beamLocsAndCounts.getOrDefault(idx + 1, 0)
                    }
                    count
                }

                else -> throw IllegalArgumentException("Unknown character: ${line[idx]}")
            }
        }
        beamLocsAndCounts = newBeamLocsAndCounts
        lineIdx++
    }

    println("Part 2: ${beamLocsAndCounts.values.sum()}")  // 187987920774390
}