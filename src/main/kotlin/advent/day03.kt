package advent

import java.lang.IllegalArgumentException

val lowercase = 'a'..'z'
val uppercase = 'A'..'Z'

fun priority(item: Char) =
  when (item) {
    in lowercase -> item - 'a' + 1
    in uppercase -> item - 'A' + 27
    else -> throw IllegalArgumentException("Unexpected item '${item}'")
  }

fun compartments(rucksack: String): Pair<CharArray, CharArray> =
  rucksack.chunked(rucksack.length / 2).let { (a, b) -> Pair(a.toCharArray(), b.toCharArray()) }

fun commonItem(compartments:Pair<CharArray,CharArray>) = compartments.first.toSet().intersect(compartments.second.toSet()).first()
fun badge(rucksacks:List<String>) = rucksacks.asSequence().map{it.toCharArray().toSet()}.reduce{a,b -> a.intersect(b)}.first()

fun processRucksacks(file:String) =
  Resource.getLines("day03/${file}")
    .asSequence()
    .chunked(3)
    .map{ badge(it)}
    .map{ priority(it)}
    .sum()

fun main() {
  println("sample ${processRucksacks("sample.txt")}")
  println("input ${processRucksacks("input.txt")}")
}