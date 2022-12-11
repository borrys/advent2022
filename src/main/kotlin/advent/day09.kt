package advent

import Resource
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.sign

enum class Direction {
  UP, DOWN, LEFT, RIGHT
}

fun String.toDirection() =
  Direction.values().find { it.name.startsWith(this) }
    ?: throw IllegalArgumentException("Unable to convert '${this}' to Direction")

data class Position(val x: Int = 0, val y: Int = 0) {
  operator fun plus(direction: Direction): Position = when (direction) {
    Direction.UP -> Position(x, y + 1)
    Direction.DOWN -> Position(x, y - 1)
    Direction.LEFT -> Position(x - 1, y)
    Direction.RIGHT -> Position(x + 1, y)
  }

  fun follow(other: Position): Position {
    val dx = other.x - this.x
    val dy = other.y - this.y
    return if (abs(dx) <= 1 && abs(dy) <= 1) this
    else Position(x + sign(dx.toFloat()).toInt(), y + sign(dy.toFloat()).toInt())
  }

  override fun toString(): String {
    return "($x, $y)"
  }

}

class Rope(
  var head: Position = Position(),
  var tail: List<Position> = (1..9).map {Position()},
  var tailHistory: Set<Position> = emptySet()
) {
  init {
    tailHistory += tail.last()
  }

  fun move(direction: Direction): Rope {
    head += direction
    tail = tail.fold(listOf(head)){prevKnots,current ->
      prevKnots+(current.follow(prevKnots.last()))
    }.drop(1)

    tailHistory += tail.last()
    return this
  }
}

class RopeOperation(val direction: Direction, val steps: Int) {
  fun applyTo(rope: Rope) = (1..steps).fold(rope) { r, _ -> r.move(direction) }
}

fun String.toRopeOperation(): RopeOperation {
  val (dir, count) = this.split(" ")
  return RopeOperation(dir.toDirection(), count.toInt())
}

fun moveRope(file: String) =
  Resource.getLines("day09/$file")
    .asSequence()
    .map { it.toRopeOperation()}
    .fold (Rope()) {rope, operation -> operation.applyTo(rope)}
    .tailHistory.size


fun main() {
  println("sample: ${moveRope("sample.txt")}")
  println("input: ${moveRope("input.txt")}")
}