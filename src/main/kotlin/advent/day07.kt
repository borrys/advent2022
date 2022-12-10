package advent

import Resource

class File(val name: String, val size: Int)

class Directory(
  val name: String,
  val parent: Directory? = null,
  val subdirs: MutableList<Directory> = mutableListOf(),
  val files: MutableList<File> = mutableListOf()
) {
  val root: Directory
    get() = parent?.root ?: this

  val size: Int
    get() = subdirs.sumOf { it.size } + files.sumOf { it.size }

  fun flatten(): List<Directory> = subdirs.flatMap { listOf(it) + it.flatten() }
}

fun changeDir(dir: Directory, path: String): Directory =
  if (path == "..") dir.parent ?: throw IllegalStateException("Trying to go one up from '${dir.name}'")
  else if (path == "/") dir.root
  else dir.subdirs.find { it.name == path }
    ?: throw IllegalStateException("No subdir with name '${path}' inside '${dir.name}'")


fun processOperation(dir: Directory, operation: String): Directory =
  if (operation.startsWith("cd")) changeDir(dir, operation.drop(3))
  else dir

fun processOutput(dir: Directory, line: String): Directory =
  if (line.startsWith("dir")) dir.also { it.subdirs += Directory(line.drop(4), it) }
  else dir.also {
    val (size, name) = line.split(" ")
    it.files += File(name, size.toInt())
  }

fun processLine(dir: Directory, line: String) =
  if (line.startsWith("$")) processOperation(dir, line.drop(2))
  else processOutput(dir, line)

fun List<String>.toDirectoryTree(): Directory {
  val root = Directory("/")
  return this.fold(root, ::processLine).let { root }
}

fun totalOfSmallDirs(file: String): Int =
  Resource.getLines("day07/$file")
    .toDirectoryTree()
    .flatten()
    .filter { it.size <= 100000 }
    .sumOf { it.size }

fun sizeOfDirToDelete(file: String): Int {
  val root = Resource.getLines("day07/$file").toDirectoryTree()
  val usedSpace = root.size
  val freeSpace = 70000000 - usedSpace
  val toClear = 30000000 - freeSpace
  return root.flatten()
    .map { it.size }
    .filter { it >= toClear }
    .minByOrNull { it } ?: usedSpace
}

fun main() {
  println("sample: ${sizeOfDirToDelete("sample.txt")}")
  println("input: ${sizeOfDirToDelete("input.txt")}")
}