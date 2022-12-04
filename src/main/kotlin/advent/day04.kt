package advent

import java.lang.IllegalArgumentException

val rangePattern = Regex("(\\d+)-(\\d+)")
fun String.toRange() =
  rangePattern.find(this)
    ?.groupValues
    ?.let { (_, lower, upper) -> lower.toInt()..upper.toInt() }
    ?: throw IllegalArgumentException("Unexpected input format '${this}'")

fun IntRange.isInside(other: IntRange): Boolean =
  other.first <= this.first && this.last <= other.last

fun IntRange.overlaps(other: IntRange): Boolean =
  this.intersect(other).isNotEmpty()

fun processAssignments(file: String) =
  Resource.getLines("day04/${file}")
    .asSequence()
    .map { it.split(",").let { chunks -> chunks.map(String::toRange) } }
    .filter { (a, b) -> a.overlaps(b) || a.isInside(b) || b.isInside(a) }
    .count()

fun main() {
  println("sample ${processAssignments("sample.txt")}")
  println("input ${processAssignments("input.txt")}")
}

