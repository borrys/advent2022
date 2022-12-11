package advent

import Resource
import java.lang.IllegalArgumentException


typealias MonkeyId = Long
typealias WorryLevel = Long

interface MonkeyOperation {
  operator fun invoke(value: WorryLevel): WorryLevel
}

class Add(private val summand: Long) : MonkeyOperation {
  override fun invoke(value: WorryLevel): WorryLevel = value + summand
}

class Multiply(private val multiplier: Long) : MonkeyOperation {
  override fun invoke(value: WorryLevel): WorryLevel = multiplier * value
}

object Square : MonkeyOperation {
  override fun invoke(value: WorryLevel): WorryLevel = value * value
}

class MonkeyTest(val testDivider: Long, val onTrue: MonkeyId, val onFalse: MonkeyId) {
  fun getNewMonkey(item: WorryLevel) = if (item % testDivider == 0L) onTrue else onFalse
}

data class Monkey(
  val id: MonkeyId,
  val items: MutableList<WorryLevel>,
  val operation: MonkeyOperation,
  val test: MonkeyTest,
  var inspectedItems: Long = 0
) {
  fun performRound(allMonkeys: List<Monkey>, worryLimiter: Long) {
    while (items.isNotEmpty()) {
      val item = items.removeFirst()
      val newItem = operation(item) % worryLimiter
      val newMonkey = test.getNewMonkey(newItem).let { newId -> allMonkeys.find { it.id == newId } }
        ?: throw IllegalStateException("Unable to find dest monkey for test $test and item $newItem")
      newMonkey.items.add(newItem)
      inspectedItems++
    }
  }
}

fun String.toMonkeyId() = """Monkey (\d+)""".toRegex()
  .find(this)?.groupValues?.get(1)
  ?.toLong()
  ?: throw IllegalArgumentException("Unable to parse MonkeyId from '${this}'")

fun String.toStartingItems(): MutableList<WorryLevel> = """\s*Starting items: ([\d\s,]+)""".toRegex()
  .find(this)?.groupValues?.get(1)
  ?.split(""", """)
  ?.map { it.toLong() }
  ?.toMutableList()
  ?: throw IllegalArgumentException("Unable to read starting items from '${this}'")


fun String.toMonkeyOperation(): MonkeyOperation = """\s*Operation: new = old ([*+]) (\d+|old)""".toRegex()
  .find(this)?.groupValues
  ?.let { (_, action, operand) ->
    when (action) {
      "*" -> when (operand) {
        "old" -> Square
        else -> Multiply(operand.toLong())
      }

      "+" -> when (operand) {
        "old" -> Multiply(2)
        else -> Add(operand.toLong())
      }

      else -> throw IllegalStateException("Unknown action '$action'")
    }
  } ?: throw IllegalArgumentException("Unable to parse operation from '$this'")

fun String.toTestId(): MonkeyId = """\s*If (true|false): throw to monkey (\d+)""".toRegex()
  .find(this)?.groupValues?.get(2)?.toLong()
  ?: throw IllegalArgumentException("Unable to parse test value from '${this}")

fun List<String>.toMonkeyTest(): MonkeyTest {
  val (testLine, onTrueLine, onFalseLine) = this
  val testDivider: Long = """\s*Test: divisible by (\d+)""".toRegex()
    .find(testLine)?.groupValues?.get(1)?.toLong()
    ?: throw IllegalArgumentException("Unable to parse test divider from '${testLine}'")
  val onTrueId: MonkeyId = onTrueLine.toTestId()
  val onFalseId: MonkeyId = onFalseLine.toTestId()

  return MonkeyTest(testDivider, onTrueId, onFalseId)
}

fun List<String>.toMonkey(): Monkey {
  val id = this[0].toMonkeyId()
  val items = this[1].toStartingItems()
  val operation = this[2].toMonkeyOperation()
  val test = this.drop(3).toMonkeyTest()
  return Monkey(id, items, operation, test)
}


fun runMonkeyBusiness(file: String): Long {
  val monkeys = Resource.getLines("day11/$file")
    .chunked(7)
    .map { it.toMonkey() }
  val worryLimiter = monkeys.map { it.test.testDivider }.fold(1L) { acc, i -> acc * i }

  (1..10000).forEach {
    monkeys.forEach { it.performRound(monkeys, worryLimiter) }
  }
  val (a, b) = monkeys.sortedByDescending { it.inspectedItems }.take(2).map { it.inspectedItems }

  return a * b
}

fun main() {
  println("sample: ${runMonkeyBusiness("sample.txt")}")
  println("input: ${runMonkeyBusiness("input.txt")}")
}