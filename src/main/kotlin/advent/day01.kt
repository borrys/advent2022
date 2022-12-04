package advent

import Resource
import kotlin.math.max

data class Result(val elves: List<Int>, val currentElf: Int)

fun run(file: String) =
  Resource.getLines("/day01/${file}").asSequence()
    .map(String::toIntOrNull)
    .fold(Result(emptyList(), 0)) { (elves, currentElf), nextPackage ->
      nextPackage?.let {Result(elves, currentElf + nextPackage)} ?: Result(elves + currentElf, 0)
    }.let {
      (elves, lastElf) ->
      (elves + lastElf).sortedDescending().take(3)
    }.sum()


fun main() {
  println("sample: ${run("sample.txt")}")
  println("input: ${run("input.txt")}")
}