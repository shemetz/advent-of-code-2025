import java.io.File

data class Field(
    val width: Int,
    val height: Int,
    val requirements: List<Int>, // count per shape index
)

fun main() {
    val lines = File("src/input.txt").readLines()
    // there are 6 shapes, each of them take 1 line (index), then 3 lines to define, then 1 blank line
    val shapeAreas = lines.take(6 * 5).chunked(5).map { shapeLines ->
        shapeLines.sumOf { line -> line.count { c -> c == '#' } }
    }
    val answer = lines.drop(6 * 5).count { line ->
        val (width, height) = line.split(":")[0].split("x").map(String::toInt)
        val requirements = line.split(":")[1].trim().split(" ").map(String::toInt)
        val totalRequiredArea = requirements.withIndex().sumOf { (shapeIndex, count) ->
            shapeAreas[shapeIndex] * count
        }
        val totalAvailableArea = width * height
        if (totalRequiredArea > totalAvailableArea) {
            return@count false
        }
        val numOfAvailable3x3Spots = (width / 3) * (height / 3)
        val numOf3x3SpotsNeededForEasyFit = requirements.sum()
        if (numOf3x3SpotsNeededForEasyFit <= numOfAvailable3x3Spots)
            return@count true
        error("this error won't happen, no difficult cases exist in the input...")
    }
    // ...it's that easy.
    // literally every field in the input is either obviously impossible (by area count) or obviously possible (by placing every shape in its own 3x3).
    // wtf, advent of code?  why is it so easy?
    // I am disappointed.
    println("Part 1 and only: $answer")  // 526
}