package advent

class SignalOperation(val cycles: Int, val registryChange: Int)

val NOOP = SignalOperation(1, 0)

fun String.toSignalOp(): SignalOperation =
  if (this == "noop") NOOP
  else {
    val (_, arg) = this.split(" ")
    SignalOperation(2, arg.toInt())
  }

class Signal(var xRegistry: Int = 1, val cycles: MutableList<Int> = mutableListOf()) {
  operator fun plus(operation: SignalOperation): Signal {
    cycles += (1..operation.cycles).map { xRegistry }
    xRegistry += operation.registryChange
    return this
  }
}

fun calculateCycles(file: String) = Resource.getLines("day10/$file").asSequence()
  .map { it.toSignalOp() }
  .fold(Signal()) { signal, op -> signal + op }
  .cycles

fun runProgram(file: String) =
  calculateCycles(file).asSequence()
    .chunked(20)
    .map { it.last() }
    .mapIndexed { idx, value -> (idx + 1) * 20 * value }
    .filterIndexed { idx, _ -> idx % 2 == 0 && idx <= 10 }
    .sum()

fun renderImage(file: String): String = calculateCycles(file)
  .asSequence()
  .mapIndexed { idx, signal -> if (idx % 40 == signal || idx % 40 == signal + 1 || idx % 40 == signal - 1) "#" else "." }
  .chunked(40)
  .map { it.joinToString("") }
  .joinToString("\n")

fun main() {
  println("sample: \n${renderImage("sample.txt")}")
  println("input: \n${renderImage("input.txt")}")
}