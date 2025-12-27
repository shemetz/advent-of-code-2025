import java.io.File

fun main() {
    var count = 0
    var value = 50
    File("src/input.txt").forEachLine { line ->
        value += line.replace("L", "-").replace("R", "+").toInt()
        value %= 100
        if (value == 0) count++
    }
    println("Part 1: $count") // 1052

    count = 0
    value = 50
    File("src/input.txt").forEachLine { line ->
        val valueWas = value
        value += line.replace("L", "-").replace("R", "+").toInt()
        if (value >= 100) count += (value / 100)
        if (value == 0) count++
        if (value < 0) count += (-value / 100) + 1
        if (value < 0 && valueWas == 0) count--
        value = ((value % 100) + 100) % 100
    }
    println("Part 2: $count") // 6295
}