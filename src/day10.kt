import java.io.File
import com.microsoft.z3.Context
import com.microsoft.z3.Status

typealias Bitmask = Int

data class Machine(
    val lighting: Bitmask,
    val buttonBitmasks: List<Bitmask>,
    val buttonLists: List<List<Int>>,
    val joltage: List<Int>
)

fun main() {
    val machines = File("src/input.txt").readLines().map { line ->
        val (
            _,
            indicatorLightDiagram,
            buttonWiringSchematics,
            joltageRequirements
        ) = Regex("\\[(.+)] (.+) \\{(.+)}").matchEntire(line)!!.groupValues
        val targetBitmask = indicatorLightDiagram
            .replace("#", "1")
            .replace(".", "0")
            .toInt(2)
        val buttonsAsIndices = buttonWiringSchematics
            .split(" ")
            .map { butt ->
                butt.drop(1)
                    .dropLast(1)
                    .split(",")
                    .map(String::toInt)
            }
        val buttonBitmasks = buttonsAsIndices
            .map { butt ->
                // convert a list of indices into a bitmask (indices start from left...)
                butt.fold(0) { acc, idx -> acc or (1 shl (indicatorLightDiagram.length - 1 - idx)) }
            }
        val whateverTheJoltageNeedsToBe = joltageRequirements.split(",").map(String::toInt)
        Machine(targetBitmask, buttonBitmasks, buttonsAsIndices, whateverTheJoltageNeedsToBe)
    }

    // we know every button should be pressed either 0 or 1 times (because 2 presses are identical to 0 presses).
    // so actually we can do this like a knapsack problem solve.
    // ALSO I just looked at the input and realized there are 10 buttons at most (0-9), nice, it's a low number so
    // we can use bit manipulation logic.  I love bits to bits
    fun knapsolve1(state: Bitmask, firstBtnIdx: Int, machineIdx: Int): Int {
        val (goal, buttons, _, _) = machines[machineIdx]
        if (state == goal) return 0
        if (firstBtnIdx >= buttons.size) return 999 // = unsolvable from here
        // two options:  either we click the first button or we don't
        val btnWiring = buttons[firstBtnIdx]
        // option 1: don't press the button
        val option1 = knapsolve1(state, firstBtnIdx + 1, machineIdx)
        // option 2: press the button
        val newState = state xor btnWiring
        val option2 = 1 + knapsolve1(newState, firstBtnIdx + 1, machineIdx)
        return minOf(option1, option2)
    }

    val answer1 = machines.indices.sumOf { idx -> knapsolve1(0b000000000, 0, idx) }
    println("Part 1: $answer1")  // 502

    fun knapsolve2Bad(state: IntArray, firstBtnIdx: Int, machineIdx: Int): Int {
        val (_, _, buttons, joltages) = machines[machineIdx]
        val deltas = state.mapIndexed { idx, _ -> joltages[idx] - state[idx] }
        if (deltas.any { it < 0 }) return 999999 // = unsolvable from here
        if (deltas.all { it == 0 }) return 0
        if (firstBtnIdx >= buttons.size) return 999999 // = unsolvable from here
        // two options:  either we click the first button at least once more, or we don't
        val btnWiring = buttons[firstBtnIdx]
        // option 1: don't press the button
        val option1 = knapsolve2Bad(state, firstBtnIdx + 1, machineIdx)
        // option 2: press the button
        val newState = state.copyOf().let {
            for (idx in btnWiring) {
                it[idx] = it[idx] + 1
            }
            it
        }
        val option2 = 1 + knapsolve2Bad(newState, firstBtnIdx, machineIdx)
        return minOf(option1, option2)
    }

    fun toStateStr(arr: IntArray): String {
        return arr.joinToString(separator = ",")
    }

    fun bfsSolve2Bad(machineIdx: Int): Int {
        val (_, _, buttons, joltages) = machines[machineIdx]
        val frontier = ArrayDeque<Pair<IntArray, Int>>()
        frontier.addLast(Pair(joltages.toIntArray(), 0))
        val seen = HashSet<String>()
        seen.add(toStateStr(joltages.toIntArray()))
        val targetStateStr = "0,".repeat(joltages.size).dropLast(1)
        while (frontier.isNotEmpty()) {
            if (frontier.size % 1000000 == 0) println(frontier.size)
            val (currState, pressesSoFar) = frontier.removeFirst()
            buttons.forEach forEachButton@{ button ->
                val newState = currState.copyOf().let {
                    for (idx in button) {
                        it[idx]--
                    }
                    it
                }
                val stateStr = toStateStr(newState)
                if ('-' in stateStr) return@forEachButton // invalid state, pressed too many times, skip
                if (stateStr == targetStateStr) {
                    return pressesSoFar + 1 // immediately return after a solution is seen, because we're using BFS
                }
                if (stateStr !in seen) {
                    seen.add(stateStr)
                    val newPair = Pair(newState, pressesSoFar + 1)
                    frontier.addLast(newPair)
                }
            }
        }
        return 999999 // unsolvable
    }


    // after two failed attempts I gave up and found out how to use z3 in kotlin.  yay...
    fun z3Solve2(machineIdx: Int): Int {
        val (_, _, buttons, joltages) = machines[machineIdx]
        Context().use { ctx ->
            val optimizer = ctx.mkOptimize()
            val variables = buttons.indices.map { idx -> ctx.mkIntConst("button_$idx") }.toTypedArray()
            // constraints 1: buttons can only be pressed 0+ times, not negative times
            variables.forEach { buttVar ->
                optimizer.Add(ctx.mkGe(buttVar, ctx.mkInt(0)))
            }
            // constraints 2: the sum of button presses affecting each light must equal the required joltage
            joltages.indices.forEach { j ->
                val terms = buttons.mapIndexed { index, idxs ->
                    ctx.mkMul(
                        variables[index],
                        ctx.mkInt(if (j in idxs) 1 else 0)
                    )
                }.toTypedArray()
                optimizer.Add(
                    ctx.mkEq(
                        // sum of relevant button presses
                        ctx.mkAdd(*terms),
                        // required joltage
                        ctx.mkInt(joltages[j])
                    )
                )
            }
            optimizer.MkMinimize(ctx.mkAdd(*variables))
            optimizer.Check()
            return variables.sumOf { optimizer.model.evaluate(it, false).toString().toInt() }
        }
    }

    val answer2 = machines.indices.sumOf { idx -> z3Solve2(idx) }
    println("Part 2: $answer2")  // 21467
}