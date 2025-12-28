import java.io.File
import kotlin.collections.filter

fun main() {
    val sum1 = File("src/input.txt").useLines { lines ->
        lines.sumOf { line ->
            val highest = line.maxBy { char -> char - '0' }
            val indexOfHighest = line.indexOf(highest)
            val highestIsLast = indexOfHighest == line.length - 1
            val secondHighestAfterHighest = if (highestIsLast)
                line.substring(0, indexOfHighest).maxBy { char -> char - '0' }
            else
                line.substring(indexOfHighest + 1).maxBy { char -> char - '0' }
            val joinedNum =
                if (highestIsLast) "$secondHighestAfterHighest$highest" else "$highest$secondHighestAfterHighest"
            joinedNum.toInt()
        }
    }

    println("Part 1: $sum1")  // 17430

    val PART_2_NUM_OF_DIGITS_NEEDED = 12
    val sum2 = File("src/input.txt").useLines { lines ->
        lines.sumOf { line ->
            val pickedCharsInOrder = mutableListOf<Char>()
            var windowLeft = 0
            var numOfDigitsLeftToPick = PART_2_NUM_OF_DIGITS_NEEDED
            while (numOfDigitsLeftToPick > 0) {
                val windowRight = line.length - numOfDigitsLeftToPick
                val nextIdxPicked = line.indices
                    .filter { idx -> idx in windowLeft..windowRight }
                    .maxBy { idx -> line[idx] - '0' }
                pickedCharsInOrder.add(line[nextIdxPicked])
                windowLeft = nextIdxPicked + 1
                numOfDigitsLeftToPick--
            }
            pickedCharsInOrder.joinToString(separator = "").toLong()
        }
    }
    println("Part 2: $sum2")  // 171975854269367
}