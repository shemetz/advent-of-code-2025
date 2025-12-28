import java.io.File

fun main() {
    val lines = File("src/input.txt").readLines()
    val operations = lines.last().trim().split(Regex("\\s+")).onEach { assert(it.length == 1) }.map { it.first() }
    val operands = lines.dropLast(1)
        .map { line ->
            line.split(Regex("\\s+"))
                .filter { it.isNotBlank() }
                .map { number ->
                    number.toLong()
                }
        }
    val rearrangedProblems1 = operations.mapIndexed { index, operation ->
        Pair(operation, operands.map { it[index] })
    }

    fun solveAndSum(problems: List<Pair<Char, List<Long>>>): Long {
        return problems.sumOf { (operation, numbers) ->
            when (operation) {
                '+' -> numbers.sum()
                '*' -> numbers.reduce { acc, n -> acc * n }
                else -> throw IllegalArgumentException("Unknown operation?! $operation")
            }
        }
    }

    val sum1 = solveAndSum(rearrangedProblems1)

    println("Part 1: $sum1")  // 4693419406682

    val maxLineLength = lines.maxOf { it.length }
    val paddedLines = lines.map { line ->
        line.padEnd(maxLineLength, ' ')
    }
    assert(paddedLines.all { it.length == maxLineLength })
    val operandsWithStartIndices =
        paddedLines.last().mapIndexed { startIdx, ch -> if (ch.isWhitespace()) null else Pair(ch, startIdx) }
            .filterNotNull()
    val operandsWithIndices = operandsWithStartIndices.zipWithNext { left, right ->
        val (ch, leftStartIdx) = left
        val (_, rightStartIdx) = right
        val leftEndIdx = rightStartIdx - 2 // inclusive, idx of last column that has data
        Triple(ch, leftStartIdx, leftEndIdx)
    } + listOf(operandsWithStartIndices.last().let { (ch, startIdx) -> Triple(ch, startIdx, maxLineLength - 1) })
    val rearrangedProblems2 = operandsWithIndices.map { (operation, startIdx, endIdx) ->
        val numbers = (startIdx..endIdx).map { idx ->
            val digits = paddedLines.dropLast(1).map { it[idx] }.filter { !it.isWhitespace() }
            val numberStr = digits.joinToString("")
            numberStr.toLong()
        }
        Pair(operation, numbers)
    }
    val sum2 = solveAndSum(rearrangedProblems2)

    println("Part 2: $sum2")  // 9029931401920
}