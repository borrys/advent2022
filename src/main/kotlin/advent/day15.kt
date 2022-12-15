package advent

import Resource
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

object Day15 {
  data class Position(val x: Int, val y: Int) {
    operator fun minus(other: Position) = abs(x - other.x) + abs(y - other.y)

    override fun toString(): String = "($x, $y)"
  }

  data class Sensor(val position: Position, val closestBeacon: Position) {
    val radius
      get() = position - closestBeacon

    override fun toString(): String {
      return "Sensor($position -> $closestBeacon)"
    }


  }

  val positionRegex = """x=(-?\d+), y=(-?\d+)""".toRegex()
  val sensorRegex = """Sensor at (.+): closest beacon is at (.+)""".toRegex()

  fun String.toPosition(): Position =
    positionRegex.find(this)?.groupValues?.let { (_, x, y) -> Position(x.toInt(), y.toInt()) }
      ?: throw IllegalArgumentException("Unable to parse Position from '$this'")

  fun String.toSensor(): Sensor = sensorRegex
    .find(this)?.groupValues?.let { (_, s, b) -> Sensor(s.toPosition(), b.toPosition()) }
    ?: throw IllegalArgumentException("Unable to parse Sensor from '$this'")

  fun Set<IntRange>.addReducing(element: IntRange): Set<IntRange> {
    val cluster =
      this.filter {
        it.contains(element.first) || it.contains(element.last) || element.contains(it.first) || element.contains(it.last)
            || (it.last + 1 == element.first)
            || (element.last + 1 == it.first)
      }
        .plusElement(element)

    val clusterMin = cluster.minOf { it.first }
    val clusterMax = cluster.maxOf { it.last }

    return minus(cluster.toSet()).plusElement(clusterMin..clusterMax)
  }

  fun findEmptySpaces(file: String, maxValue: Int): Long? {
    val sensors = Resource.getLines("day15/$file")
      .map { it.toSensor() }

    return (0..maxValue).asSequence().map { it to findRestricted(sensors, it, maxValue) }
      .find { (_, x) -> x != null }
      ?.let { (y, x) -> x!!.toLong() * 4000000 + y }
  }

  private fun findRestricted(sensors: List<Sensor>, y: Int, maxValue: Int): Int? {
    val beaconsOnY = sensors.asSequence().filter { it.closestBeacon.y == y }.map { it.closestBeacon.x }.toSet().map { it..it }
      .fold(setOf<IntRange>()) { acc, it -> acc.addReducing(it) }
    val sensorsOnY = sensors.asSequence().filter { it.position.y == y }.map { it.position.x }.toSet().map { it..it }
      .fold(setOf<IntRange>()) { acc, it -> acc.addReducing(it) }
    val occupied = beaconsOnY.fold(sensorsOnY) { acc, it -> acc.addReducing(it) }

    return sensors.asSequence()
      .filter {
        val distanceY = abs(it.position.y - y)
        distanceY <= it.radius
      }
      .map {
        val distanceY = abs(it.position.y - y)
        val distanceX = abs(it.radius - distanceY)
        (it.position.x - distanceX)..(it.position.x + distanceX)
      }
      .fold(occupied) { acc, subRng ->
        acc.addReducing(subRng)
      }
      .let {
        it.filter { it.first <= maxValue && it.last >=0 }.map {max(0, it.first)..min(it.last, maxValue)}.sortedBy { it.first }
      }
      .let {
        if(it.size==2) println("ranges: $it")

        if (it.isEmpty() || it.size > 2) null
        else if (it.size == 2) it.first().last + 1
        else if (it.first().first==1) 0
        else if (it.first().last==maxValue-1) maxValue
        else null
      }
  }

}

fun main() {
//  println("sample: ${Day15.findEmptySpaces("sample.txt", 20)}")
  println("input: ${Day15.findEmptySpaces("input.txt", 4000000)}")
}
