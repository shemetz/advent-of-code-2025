import java.io.File
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    var sum = 0.toLong()
    val line = File("src/input.txt").readLines()[0]
    val ranges = line
        .split(",")
        .map { s -> s.split("-")
            .map { it.toLong() }
        }

    for (range in ranges) {
        for (num in range[0]..range[1]) {
            val numLengthBase10 = log10(num.toDouble()).toLong() + 1
            if (numLengthBase10 % 2 == 1.toLong()) continue
            val tenToTheHalfLog = 10.toDouble().pow((numLengthBase10 / 2).toInt()).toLong()
            val lowerDigits = num % tenToTheHalfLog
            val upperDigits = num / tenToTheHalfLog
            if (lowerDigits == upperDigits)
                sum += num
        }
    }

    println("Part 1: $sum")  // 22062284697

    sum = 0

    for (range in ranges) {
        for (num in range[0]..range[1]) {
            val numLengthBase10 = (log10(num.toDouble()).toLong() + 1).toInt()
            val numAsStr = num.toString()
            for (k in 1..(numLengthBase10 - 1)) {
                if (numLengthBase10 % k != 0) continue
                if (numAsStr == numAsStr.substring(0, k).repeat(numLengthBase10 / k)) {
                    sum += num
                    break
                }
            }
        }
    }

    println("Part 2: $sum") // 46666175279
}