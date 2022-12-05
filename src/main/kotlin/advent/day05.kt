package advent

typealias Crate = Char
typealias Stack = MutableList<Crate>


class Operation(val quantity: Int, val from: Int, val to: Int) {
  companion object {
    private val pattern = Regex("move (\\d+) from (\\d+) to (\\d+)")
    fun parse(line: String): Operation = pattern
      .find(line)
      ?.groupValues
      ?.let { (_, qty, from, to) -> Operation(qty.toInt(), from.toInt(), to.toInt()) }
      ?: throw IllegalStateException("Unable to parse operation $line")
  }

  operator fun invoke(stacks: List<Stack>): List<Stack> {
      val crates = stacks[from-1].let {stack ->
        val movedCrates = stack.takeLast(quantity)
        repeat(quantity){stack.removeLast()}
        movedCrates
      }

      stacks[to-1].addAll(crates)

    return stacks
  }
}

val cratePattern = """((\[([A-Z])])|(\s{3}))\s?""".toRegex()
fun String.toCrates(): List<Crate?> = this.chunked(4)
  .map{cratePattern.find(it)
    ?.groupValues
    ?.let {groups -> groups[3].getOrNull(0)}}

fun parseStacks(definition: List<String>): List<Stack> {
  val emptyStacks = definition.last().chunked(4).last().trim().toInt().let {
    (1..it).map { mutableListOf<Crate>() }
  }
  val bottomUp = definition.asReversed().drop(1)
  return bottomUp
    .map { it.toCrates() }
    .fold(emptyStacks) { stacks, row ->
      row.forEachIndexed { index, crate -> if (crate != null)stacks[index].add(crate)}
      stacks
    }
}


val Stack.top: Crate
  get() = this.last()

fun computeStacks(file: String): String {
  val lines = Resource.getLines("day05/${file}")
  val stacksDefinition = lines.takeWhile { it.isNotEmpty() }
  val operationsDefinition = lines.drop(stacksDefinition.size + 1)

  val stacks = parseStacks(stacksDefinition)
  return operationsDefinition.asSequence().map { Operation.parse(it) }
    .fold(stacks) { agg, op -> op(agg) }
    .map { it.top }.joinToString("")
}

fun main() {
  println("sample ${computeStacks("sample.txt")}")
  println("input ${computeStacks("input.txt")}")
}