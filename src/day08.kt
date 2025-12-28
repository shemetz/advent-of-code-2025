import java.io.File

fun pow2(x: Long): Long = x * x

fun main() {
    val points = File("src/input.txt").readLines().map { line ->
        line.split(",")
            .map(String::toLong)
            .let { coords -> Triple(coords[0], coords[1], coords[2]) }
    }
    // this calls for a disjoint set / union-find data structure, with a "forest" made of disjoint sets of points,
    // and each set is represented by one of its points
    // (a point is a "junction box", and a set of them is a "circuit", in today's AoC terms)
    val representativeByPoint = points.associateWith { it }.toMutableMap()
    val disjointSetsByRepresentative = points.associateWith { setOf(it) }.toMutableMap()
    val closePairsChecked = mutableSetOf<Pair<Triple<Long, Long, Long>, Triple<Long, Long, Long>>>()
//    val numOfConnectionsToMake = 10 // 10 for example, 1000 for real input
    val numOfConnectionsToMake = 1000 // 10 for example, 1000 for real input
    repeat(numOfConnectionsToMake) { i ->
        if (i % (numOfConnectionsToMake / 10) == 0)
            println("Calculating... (${i}/$numOfConnectionsToMake), circuit count: ${disjointSetsByRepresentative.size}")
        assert(disjointSetsByRepresentative.size >= 3)
        var minDistancePow2 = Long.MAX_VALUE
        var closestPairNotConnected = Pair(
            disjointSetsByRepresentative.entries.first().key,
            disjointSetsByRepresentative.entries.last().key
        )
        disjointSetsByRepresentative.values.forEach loopEverySet@{ set1 ->
            set1.forEach loopEveryPoint@{ point1 ->
                disjointSetsByRepresentative.values.forEach loopEveryPointAndSet@{ set2 ->
                    // this is commented out on purpose!  we want to count roundabout potential connections in the repeat
                    //     if (set2.contains(point1)) return@loopEveryPointAndSet
                    set2.forEach loopEveryPointAndPoint@{ point2 ->
                        if (point1 == point2) return@loopEveryPointAndPoint
                        if (Pair(point1, point2) in closePairsChecked) return@loopEveryPointAndPoint
                        if (Pair(point2, point1) in closePairsChecked) return@loopEveryPointAndPoint
                        val distancePow2 = pow2(point1.first - point2.first) +
                                pow2(point1.second - point2.second) +
                                pow2(point1.third - point2.third)
                        if (distancePow2 < minDistancePow2) {
                            minDistancePow2 = distancePow2
                            closestPairNotConnected = Pair(point1, point2)
                        }
                    }
                }
            }
        }
        val (pointA, pointB) = closestPairNotConnected
        val repA = representativeByPoint[pointA]!!
        val repB = representativeByPoint[pointB]!!
        if (repA == repB) {
            closePairsChecked.add(Pair(pointA, pointB))
            return@repeat // do nothing, already connected
        }
        closePairsChecked.add(Pair(pointA, pointB))
        // union the two sets (= connect circuits)
        val setA = disjointSetsByRepresentative[repA]!!
        val setB = disjointSetsByRepresentative[repB]!!
        val newSet = setA + setB
        val newRepresentative = repA // could be any point from newSet, doesn't matter
        newSet.forEach { point ->
            representativeByPoint[point] = newRepresentative
        }
        disjointSetsByRepresentative.remove(repA)
        disjointSetsByRepresentative.remove(repB)
        disjointSetsByRepresentative[newRepresentative] = newSet
    }
    val threeBiggestSetSizes = disjointSetsByRepresentative.values
        .sortedByDescending { it.size }
        .take(3)
        .map { it.size }
    println("Part 1: ${threeBiggestSetSizes.reduce { a, b -> a * b }}")  // 123420


    // Reset and do it again for part 2, but this time repeat until they're ALL connected and find latest 2
    representativeByPoint.clear()
    points.forEach { representativeByPoint[it] = it }
    disjointSetsByRepresentative.clear()
    points.forEach { disjointSetsByRepresentative[it] = setOf(it) }
    var latestPairToConnect = Pair(points[0], points[0]) // ignore this initial value
    while (disjointSetsByRepresentative.size > 1) {
        if (disjointSetsByRepresentative.size % 100 == 0 || disjointSetsByRepresentative.size <= 5)
            println("Calculating... circuit count: ${disjointSetsByRepresentative.size}")
        // find two closest points that belong to different sets
        var minDistancePow2 = Long.MAX_VALUE
        var closestPairNotConnected = Pair(
            disjointSetsByRepresentative.entries.first().key,
            disjointSetsByRepresentative.entries.last().key
        )
        disjointSetsByRepresentative.values.forEach loopEverySet@{ set1 ->
            set1.forEach loopEveryPoint@{ point1 ->
                disjointSetsByRepresentative.values.forEach loopEveryPointAndSet@{ set2 ->
                    // this is no longer commented out!  we want to optimize
                    if (set2.contains(point1)) return@loopEveryPointAndSet
                    set2.forEach loopEveryPointAndPoint@{ point2 ->
                        assert(point1 != point2)
                        val distancePow2 = pow2(point1.first - point2.first) +
                                pow2(point1.second - point2.second) +
                                pow2(point1.third - point2.third)
                        if (distancePow2 < minDistancePow2) {
                            minDistancePow2 = distancePow2
                            closestPairNotConnected = Pair(point1, point2)
                        }
                    }
                }
            }
        }
        val (pointA, pointB) = closestPairNotConnected
        val repA = representativeByPoint[pointA]!!
        val repB = representativeByPoint[pointB]!!
        assert(repA != repB)
        // union the two sets (= connect circuits)
        val setA = disjointSetsByRepresentative[repA]!!
        val setB = disjointSetsByRepresentative[repB]!!
        val newSet = setA + setB
        val newRepresentative = repA // could be any point from newSet, doesn't matter
        newSet.forEach { point ->
            representativeByPoint[point] = newRepresentative
        }
        disjointSetsByRepresentative.remove(repA)
        disjointSetsByRepresentative.remove(repB)
        disjointSetsByRepresentative[newRepresentative] = newSet
        latestPairToConnect = Pair(pointA, pointB)
    }
    println("Part 2: ${(latestPairToConnect.first.first * latestPairToConnect.second.first)}")  // 673096646
}