package advent

import Resource

data class ElvesInventory(val elves: List<Int>, val currentElf: Int)

fun run(file: String) =
  Resource.getLines("/day01/${file}").asSequence()
    .map(String::toIntOrNull)
    .fold(ElvesInventory(emptyList(), 0)) { (elves, currentElf), nextPackage ->
      nextPackage?.let {ElvesInventory(elves, currentElf + nextPackage)} ?: ElvesInventory(elves + currentElf, 0)
    }.let {
      (elves, lastElf) ->
      (elves + lastElf).sortedDescending().take(3)
    }.sum()


fun main() {
  println("sample: ${run("sample.txt")}")
  println("input: ${run("input.txt")}")
}