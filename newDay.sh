#!/bin/bash

day=$1

printf "%s\n" \
  "package advent" \
  "" \
  "import Resource" \
  "" \
  "object Day$day {"\
  "  fun execute(file: String) ="\
  "    Resource.getLines(\"day$day/\$file\")"\
  "}"\
  "" \
  "fun main() {"\
  "  println(\"sample: \${Day$day.execute(\"sample.txt\")}\")"\
  "  // println(\"input: \${Day$day.execute(\"input.txt\")}\")"\
  "}"> src/main/kotlin/advent/day$day.kt

mkdir "src/main/resources/day$day"
touch "src/main/resources/day$day/sample.txt"
touch "src/main/resources/day$day/input.txt"

git add -A