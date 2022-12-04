package advent

import Resource
import java.lang.IllegalArgumentException


enum class MatchResult(val score: Int) {
  WIN(6), DRAW(3), LOSS(0);

  operator fun minus(opponent: Weapon): Weapon =
    when (this) {
      DRAW -> opponent
      WIN -> Weapon.forOrdinal(opponent.ordinal + 1)
      LOSS -> Weapon.forOrdinal(opponent.ordinal + 2)
    }
}

enum class Weapon(public val score: Int) {
  ROCK(1), PAPER(2), SCISSORS(3);

  companion object {
    fun forOrdinal(ordinal: Int) = values()[ordinal % values().size]
  }

  private fun matchResult(other: Weapon) = when {
    this == other -> MatchResult.DRAW
    this.ordinal == (other.ordinal + 1) % Weapon.values().size -> MatchResult.WIN
    else -> MatchResult.LOSS
  }

  operator fun minus(other: Weapon): Int = matchResult(other).score + this.score
}

fun parseOpponent(input: String): Weapon = when (input) {
  "A" -> Weapon.ROCK
  "B" -> Weapon.PAPER
  "C" -> Weapon.SCISSORS
  else -> throw IllegalArgumentException("Unrecognized opponents weapon '${input}'")
}

fun parseSelf(input: String): Weapon = when (input) {
  "X" -> Weapon.ROCK
  "Y" -> Weapon.PAPER
  "Z" -> Weapon.SCISSORS
  else -> throw IllegalArgumentException("Unrecognized own weapon '${input}'")
}

fun parseResult(input: String): MatchResult = when (input) {
  "X" -> MatchResult.LOSS
  "Y" -> MatchResult.DRAW
  "Z" -> MatchResult.WIN
  else -> throw IllegalArgumentException("Unrecognized result '${input}'")
}

fun fight(file: String): Int =
  Resource.getLines("day02/${file}")
    .asSequence()
    .map { it.split(" ") }
    .map { (opponent, result) -> Pair(parseOpponent(opponent), parseResult(result)) }
    .map { (opponent, result) -> (result - opponent).score + result.score }
    .sum()

fun main() {
  println("sample: ${fight("sample.txt")}")
  println("sample: ${fight("input.txt")}")
}