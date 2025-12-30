import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.jetbrains.kotlinx.multik.ndarray.operations.sumBy
import java.io.File

data class Field(
    val width: Int,
    val height: Int,
    val requirements: List<Int>, // count per shape index
)

data class Shape(
    val pattern: NDArray<Int, D2>, // 3x3 array of 1s and 0s
    val area: Int, // number of true cells
)


fun main() {
    val lines = File("src/input.txt").readLines()
    // there are 6 shapes, each of them take 1 line (index), then 3 lines to define, then a blank line
    val shapes = lines.take(6 * 5).chunked(5).map { shapeLines ->
        val shapeArray = mk.d2array<Int>(3, 3) { 1 }
        for (r in 0..<3)
            for (c in 0..<3) {
                shapeArray[r, c] = if (shapeLines[r + 1][c] == '#') 1 else 0
            }
        Shape(shapeArray, mk.math.sum(shapeArray))
    }
    val allFields = lines.drop(6 * 5).map { line ->
        val dimensions = line.split(":")[0].split("x").map(String::toInt)
        val requirements = line.split(":")[1].trim().split(" ").map(String::toInt)
        Field(dimensions[0], dimensions[1], requirements)
    }

    // first, let's try to pack 1 of every shape into as small of a space as possible.
    // each shape fits in a 3x3 so we know it's always possible in these space sizes:
    // - 9x6 (two rows of three shapes each)
    // - 18x3 (one row of six shapes)
    // - 15x3 (more cleverly packed row of six shapes)
    // (those will be the maximum sizes)
    // and we know the minimum "size" (total area) is a perfect packing, we'll add up areas for that
    val minimumAreaForPacking = shapes.sumOf { it.area }
    val maximumAreaForPacking = 9 * 6
    println("Minimum area to pack all shapes: $minimumAreaForPacking")
    // NOTE:  I'm limiting myself to rect packings, not gonna bother with tilings even though they likely help
    // NOTE: to reduce permutation count, limit to width >= height
    val possiblePackings: List<NDArray<Int, D2>> = emptyList()
    for (width in 5..13) {
        for (height in 3..6) {
            val area = width * height
            if (area < minimumAreaForPacking) continue
            if (area > maximumAreaForPacking) continue
            if (height > width) continue
            // try to pack shapes into width x height
            val fieldArray = mk.d2array(height, width) { false }
            shapes.for
        }
    }
}