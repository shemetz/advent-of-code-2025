import java.io.File
import kotlin.math.absoluteValue

data class Point2D(val x: Long, val y: Long)

/**
 * (will not combine elements with themselves, will not produce redundant mirror pairs)
 */
fun <T> everyCombinationOfSize2(list: List<T>): Sequence<Pair<T, T>> =
    sequence {
        for (i in list.indices) {
            for (j in i + 1..<list.size) {
                yield(Pair(list[i], list[j]))
            }
        }
    }

fun calcRectSizeBetweenPoints(p1: Point2D, p2: Point2D): Long {
    val dx = (p1.x - p2.x).absoluteValue + 1
    val dy = (p1.y - p2.y).absoluteValue + 1
    return dx * dy
}

fun main() {
    val points = File("src/input.txt").readLines()
        .map { line -> line.split(",") }
        .map { Point2D(it[0].toLong(), it[1].toLong()) }
    val largestRectSize = everyCombinationOfSize2(points)
        .maxOf { (p1, p2) -> calcRectSizeBetweenPoints(p1, p2) }
    println("Part 1: $largestRectSize")  // 4773451098

    // plan:  try every rect again, but ignore any rect that intersects with any orthogonal line in the "polygon" path
    val path = points.zipWithNext() + Pair(points.last(), points.first()) // note: it's cyclical
    val largestValidRectSize = everyCombinationOfSize2(points)
        .filter { (p1, p2) ->
            val minX = minOf(p1.x, p2.x) + 1
            val maxX = maxOf(p1.x, p2.x) - 1
            val minY = minOf(p1.y, p2.y) + 1
            val maxY = maxOf(p1.y, p2.y) - 1
            // check each segment in the path for intersection with the rectangle defined by (minX, minY) to (maxX, maxY)
            for ((start, end) in path) {
                if (start.x == end.x) {
                    // vertical line
                    if (start.x in minX..maxX) {
                        val lineMinY = minOf(start.y, end.y)
                        val lineMaxY = maxOf(start.y, end.y)
                        if (lineMaxY >= minY && lineMinY <= maxY) {
                            return@filter false
                        }
                    }
                } else if (start.y == end.y) {
                    // horizontal line
                    if (start.y in minY..maxY) {
                        val lineMinX = minOf(start.x, end.x)
                        val lineMaxX = maxOf(start.x, end.x)
                        if (lineMaxX >= minX && lineMinX <= maxX) {
                            return@filter false
                        }
                    }
                }
            }

            // no intersections found, meaning the insode of the rectangle is either fully inside or fully outside the path
            // in the sample input the only outer rect to test is smaller than the answer, so the next step is not necessary,
            // but I'll do it anyway to have a fully correct solution:

            // we'll test a point inside the rectangle to see if it's inside the polygon, via standard raycasting
            // (towards +X infinity)
            val testPoint = Point2D(minX + 1, minY + 1)
            var intersectionCounter = 0
            for ((start, end) in path) {
                if (start.y == end.y) {
                    // horizontal lines get ignored
                    continue
                } else if (start.x == end.x) {
                    // vertical line
                    val lineMinY = minOf(start.y, end.y)
                    val lineMaxY = maxOf(start.y, end.y)
                    // if the line is to the right of the test point and crosses its Y level, count an intersection
                    if (testPoint.y in lineMinY..lineMaxY && start.x > testPoint.x) {
                        intersectionCounter++
                    }
                }
            }
            if (intersectionCounter % 2 == 0) {
                // an even number of intersections means that the point is outside the polygon
                return@filter false
                // TODO test one day if this code actually works (untestable because my input doesn't require it to work)
            }
            return@filter true
        }
        .maxOf { (p1, p2) -> calcRectSizeBetweenPoints(p1, p2) }
    println("Part 2: $largestValidRectSize")  // 1429075575
}