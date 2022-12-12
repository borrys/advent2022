package advent

import Resource

data class Coordinates(val x: Int, val y: Int) {
  val up get() = Coordinates(x, y - 1)
  val down get() = Coordinates(x, y + 1)
  val left get() = Coordinates(x - 1, y)
  val right get() = Coordinates(x + 1, y)

  val neighbours get() = listOf(up, down, left, right)

  override fun toString(): String {
    return "($x, $y)"
  }
}

typealias Height = Char

class Terrain(val heights: Map<Coordinates, Height>) {
  val start: Coordinates = heights.entries.find { entry -> entry.value == 'S' }?.key
    ?: throw IllegalStateException("No Starting point found on the map")
  val end: Coordinates = heights.entries.find { entry -> entry.value == 'E' }?.key
    ?: throw IllegalStateException("No Starting point found on the map")

  fun findPath(startPoint:Coordinates = start): List<Coordinates>? {
    val visited = mutableListOf(startPoint)
    val previous = mutableMapOf(startPoint to startPoint)

    while (visited.isNotEmpty()) {
      visited.sortBy { pathLength(previous, it,startPoint) }
      val point = visited.removeFirst()
      if (point == end) break

      val len = pathLength(previous, point,startPoint) + 1

      validNeighbours(point).filter { !previous.containsKey(it) || pathLength(previous, it,startPoint) > len }.forEach {
        previous[it] = point
        visited.add(it)
      }
    }

    if (previous[end] == null) return null
    return generateSequence(end) { previous[it] }.takeWhile { it != startPoint }.toList();
  }

  private fun pathLength(previous: Map<Coordinates, Coordinates>, point: Coordinates, startPoint:Coordinates): Int =
    generateSequence(point) { previous[it] }.takeWhile { it != startPoint }.take(10).toList().size

  private fun validNeighbours(point: Coordinates): List<Coordinates> {
    val pointHeight = heights[point] ?: return emptyList()

    return point.neighbours.filter { n ->
      val nHeight = heights[n]
      if (nHeight == null) false
      else if (pointHeight == 'S' || nHeight == 'E') true
      else (nHeight - pointHeight) <= 1
    }
  }
}

fun List<String>.toTerrain(): Terrain =
  this.foldIndexed(mutableMapOf<Coordinates, Height>()) { y, acc, line ->
    line.foldIndexed(acc) { x, map, height ->
      map[Coordinates(x, y)] = height
      map
    }
  }.let { Terrain(it) }


fun shortestPath(file: String) =
  Resource.getLines("day12/$file").toTerrain().findPath()?.size


fun bestTrail(file: String) =
  Resource.getLines("day12/$file").toTerrain().let{terrain->
    terrain.heights.filter { it.value == 'a' || it.value == 'S' }
      .map { it.key }.minOfOrNull { terrain.findPath(it)?.size ?: Int.MAX_VALUE }
  }

fun main() {
  println("sample: ${bestTrail("sample.txt")}")
  println("input: ${bestTrail("input.txt")}")
}
