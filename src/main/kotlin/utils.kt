object Resource {
  fun getLines(path: String):List<String> =
    this.javaClass.getResource(path)?.readText()?.lines() ?: emptyList()

}