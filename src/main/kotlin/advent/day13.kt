package advent

import Resource


sealed interface Packet {
  fun isBefore(other: Packet): Boolean?
}

class SingleNumber(val value: Int) : Packet {
  override fun isBefore(other: Packet): Boolean? {
    return when (other) {
      is SingleNumber -> if (this.value < other.value) true else if (this.value > other.value) false else null
      is PacketsList -> PacketsList(mutableListOf(this)).isBefore(other)
    }
  }

  override fun toString(): String = "$value"
}

class PacketsList(val packets: MutableList<Packet>, val parent: PacketsList? = null) : Packet {
  override fun isBefore(other: Packet): Boolean? {
    return when (other) {
      is SingleNumber -> this.isBefore(PacketsList(mutableListOf(other)))
      is PacketsList -> isBeforePL(other)
    }
  }

  private fun isBeforePL(other: PacketsList): Boolean? =
    this.packets.zip(other.packets).asSequence()
      .map { (l, r) -> l.isBefore(r) }
      .find { it != null }
      ?: SingleNumber(this.packets.size).isBefore(SingleNumber(other.packets.size))

  override fun toString(): String = "$packets"
}

val tokensRegex = """(?<start>\[)|(?<end>])|(?<separator>,)|(?<num>\d+)""".toRegex()

fun kotlin.text.MatchResult.hasGroup(g: String) = this.groups[g]?.value?.isNotEmpty() ?: false

fun String.toPacketsList(): PacketsList {
  val root = PacketsList(mutableListOf(), null)
  tokensRegex.findAll(this.drop(1).dropLast(1))
    .fold(root) { acc, token ->
      if (token.hasGroup("start"))
        PacketsList(mutableListOf(), acc).also { acc.packets += it }
//          .also { println("starting group $root") }
      else if (token.hasGroup("end"))
        acc.parent
//          .also { println("closing group $root") }
          ?: throw IllegalStateException("closing not opened list")
      else if (token.hasGroup("num"))
        acc.also { acc.packets += SingleNumber(token.value.toInt()) }
//          .also { println("number $root") }
      else
        acc
//          .also { println("separator $root") }

    }
  return root
}

fun validPairs(file: String) =
  Resource.getLines("day13/$file").asSequence()
    .chunked(3)
    .map { it.take(2).map(String::toPacketsList).toList() }
    .mapIndexed { idx, (a, b) -> (idx + 1) to (a.isBefore(b) ?: false) }
    .filter { (_, result) -> result }
    .sumOf { (idx, _) -> idx }

fun getDecoderKey(file: String): Int {
  val packets = Resource.getLines("day13/$file").asSequence()
    .filter { it.isNotBlank() }
    .map { it.toPacketsList() }
  val divider1 = "[[2]]".toPacketsList()
  val divider2 = "[[6]]".toPacketsList()
  val sorted = (packets + divider1 + divider2).sortedWith { a, b ->
    when (a.isBefore(b)) {
      true -> -1
      null -> 0
      false -> 1
    }
  }

  return (sorted.indexOf(divider1) + 1) * (sorted.indexOf(divider2) + 1)
}

fun main() {
  println("sample: ${getDecoderKey("sample.txt")}")
  println("input: ${getDecoderKey("input.txt")}")
}