package advent

import Resource

data class Tree(val height: Int, var visible: Boolean = false) {
  fun toVisible(): Tree {
    this.visible = true
    return this
  }
}
typealias Forest = Array<Array<Tree>>

fun loadForest(file: String): Forest = Resource.getLines("day08/$file")
  .map { it.toCharArray().map(Char::digitToInt).map(::Tree).toTypedArray() }.toTypedArray()

fun visibleTrees(file: String): Int {
  val forest: Forest = loadForest(file)

  forest[0].forEach { it.toVisible() }
  forest.last().forEach { it.toVisible() }
  forest.forEach { it[0].toVisible(); it.last().toVisible() }

  val rowsToCheck = forest.drop(1).dropLast(1)
  rowsToCheck.forEach { treesRow ->
    val treesToCheck = treesRow.drop(1).dropLast(1)
    treesToCheck.fold(treesRow.first().height) { maxHeight, tree ->
      if (tree.height <= maxHeight) maxHeight
      else tree.toVisible().height
    }
    treesToCheck.reversed().fold(treesRow.last().height) { maxHeight, tree ->
      if (tree.height <= maxHeight) maxHeight
      else tree.toVisible().height
    }
  }

  val columnsToCheck = 1..(forest.first().size - 2)
  columnsToCheck.forEach { columnIdx ->
    rowsToCheck.fold(forest.first()[columnIdx].height) { maxHeight, treeRow ->
      val tree = treeRow[columnIdx]
      if (tree.height <= maxHeight)
        maxHeight
      else
        tree.toVisible().height
    }
    rowsToCheck.reversed().fold(forest.last()[columnIdx].height) { maxHeight, treeRow ->
      val tree = treeRow[columnIdx]
      if (tree.height <= maxHeight) maxHeight
      else tree.toVisible().height
    }
  }

  return forest.sumOf { it.count(Tree::visible) }
}

fun countVisible(maxHeight: Int): (List<Int>) -> Int = { trees ->
  trees.takeWhile { it < maxHeight }.size.let {
    if (it == trees.size) it else it + 1
  }
}

fun score(forest: Forest, rowIdx: Int, colIdx: Int): Int {
  val row = forest[rowIdx]
  val selectedHeight = row[colIdx].height
  val count = countVisible(selectedHeight)
  val left = (0 until colIdx).reversed().map { row[it ].height }.let { count(it) }
  val right = (colIdx + 1 until row.size).map { row[it ].height }.let { count(it) }
  val top = (0 until rowIdx).reversed().map { forest[it][colIdx].height }.let { count(it) }
  val bottom = (rowIdx + 1 until forest.size).map { forest[it][colIdx].height }.let { count(it) }

  val result = left * right * top * bottom
   return result
}

fun findBestTree(file: String): Int {
  val forest = loadForest(file)

  val rows = 1..(forest.size - 2)
  val columns = 1..(forest.first().size - 2)

  return rows.flatMap { row ->
    columns.map { col ->
      score(forest, row, col)
    }
  }.max()
}

fun main() {
  println("sample: ${findBestTree("sample.txt")}")
  println("input: ${findBestTree("input.txt")}")
}