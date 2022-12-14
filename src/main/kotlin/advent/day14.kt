package advent

import Resource


object Day14 {
  data class Position(val x: Int, val y: Int) {
    fun fallDown(cave: Cave): Position? {
      val down = Position(x, y + 1)
      val downLeft = Position(x - 1, y + 1)
      val downRight = Position(x + 1, y + 1)

      val newPosition = listOf(down, downLeft, downRight).find { cave.isEmpty(it) }

      return if (newPosition != null) newPosition.fallDown(cave)
      else this
    }

    override fun toString(): String {
      return "($x, $y)"
    }

  }

  class Stone(val corners: List<Position>) {
    val xRange = corners.minOf { it.x }..corners.maxOf { it.x }
    val yRange = corners.minOf { it.y }..corners.maxOf { it.y }
    fun contains(position: Position): Boolean =
      xRange.contains(position.x) &&
          yRange.contains(position.y) &&
          corners.windowed(2)
            .any { (start, end) ->
              (start.x..end.x).toValid().contains(position.x) && (start.y..end.y).toValid().contains(position.y)
            }

  }

  fun IntRange.toValid() = if (first <= last) this else last..first

  class Cave(val stones: List<Stone>, val sand: MutableSet<Position> = mutableSetOf()) {
    val maxDepth = stones.flatMap { it.corners.map(Position::y) }.max()

    operator fun plusAssign(sandParticle: Position) {
      sand += sandParticle
    }

    fun isEmpty(position: Position) =
      position.y < maxDepth + 2 && !sand.contains(position) && stones.none { it.contains(position) }
  }

  fun String.toPosition() = this.split(",").map { it.toInt() }.let { (x, y) -> Position(x, y) }

  fun String.toStone() = this.split("->").map { it.trim().toPosition() }.let { Stone(it) }


  fun countSandParticles(file: String): Int {
    val cave = Resource.getLines("day14/$file").map { it.toStone() }.let { Cave(it) }
    val source = Position(500, 0)
    return generateSequence { source }
      .map { it.fallDown(cave)?.also { finalPosition -> cave += finalPosition } }
      .takeWhile { it != source }
      .count() + 1
  }
}


fun main() {
//  println("sample: ${Day14.countSandParticles("sample.txt")}")
  println("input: ${Day14.countSandParticles("input.txt")}")
}
