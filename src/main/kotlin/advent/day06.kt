package advent

fun List<Char>.unique() = this.toSet().size == this.size

fun findMarker(file: String, markerLength:Int = 4) =
  Resource.getLines("day06/${file}").first()
    .toCharArray().toList()
    .asSequence()
    .windowed(markerLength)
    .indexOfFirst(List<Char>::unique) + markerLength


fun main() {
  println("sample: ${findMarker("sample.txt", 14)}")
  println("sample: ${findMarker("input.txt",14)}")
}