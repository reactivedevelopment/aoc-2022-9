package com.adventofcode

import com.adventofcode.Game.pointsVisitedByTail
import com.adventofcode.Game.step
import java.awt.geom.Point2D.distance

data class Point(val x: Int, val y: Int) {

  fun left() = copy(x = x - 1)

  fun right() = copy(x = x + 1)

  fun up() = copy(y = y + 1)

  fun down() = copy(y = y - 1)

  private fun upRight() = right().up()

  private fun upLeft() = left().up()

  private fun downRight() = right().down()

  private fun downLeft() = left().down()

  fun crosswalk() = setOf(
    left(),
    right(),
    down(),
    up(),
  )

  fun diagonals() = setOf(
    upRight(),
    upLeft(),
    downRight(),
    downLeft(),
  )

  fun onSameVerticalOrHorizontal(other: Point): Boolean {
    if (other in crosswalk()) {
      return true
    }
    return other in setOf(right().right(), left().left(), up().up(), down().down())
  }

  fun distanceTo(other: Point): Double {
    val x1 = x.toDouble()
    val y1 = y.toDouble()
    val x2 = other.x.toDouble()
    val y2 = other.y.toDouble()
    return distance(x1, y1, x2, y2)
  }

  fun isTouching(other: Point): Boolean {
    return this == other || other in crosswalk() || other in diagonals()
  }
}

fun Set<Point>.nearestTo(p: Point): Point {
  return minBy {
    it.distanceTo(p)
  }
}

object Game {

  private val pointsVisitedByTail = mutableSetOf(Point(0, 0))

  private var head = Point(0, 0)

  private var tail = Point(0, 0)
    set(value) {
      field = value
      pointsVisitedByTail.add(value)
    }

  private fun tailStep() {
    if (tail.isTouching(head)) {
      return
    }
    if (tail.onSameVerticalOrHorizontal(head)) {
      tail = tail.crosswalk().nearestTo(head)
      return
    }
    tail = tail.diagonals().nearestTo(head)
  }

  private fun headStep(direction: String) {
    head = when (direction) {
      "L" -> head.left()
      "R" -> head.right()
      "U" -> head.up()
      "D" -> head.down()
      else -> error("Unknown direction $direction")
    }
  }

  fun step(direction: String) {
    headStep(direction)
    tailStep()
  }

  fun pointsVisitedByTail(): Int {
    return pointsVisitedByTail.size
  }
}


fun process(line: String) {
  val (direction, count) = line.split(" ")
  repeat(count.toInt()) {
    step(direction)
  }
}

fun solution(): Int {
  return pointsVisitedByTail()
}

fun main() {
  ::main.javaClass
    .getResourceAsStream("/input")!!
    .bufferedReader()
    .forEachLine(::process)
  println(solution())
}
