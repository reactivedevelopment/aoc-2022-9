package com.adventofcode

import java.awt.geom.Point2D.distance

data class Point(val x: Int, val y: Int) {

  fun left() = copy(x = x - 1)

  fun right() = copy(x = x + 1)

  fun up() = copy(y = y + 1)

  fun down() = copy(y = y - 1)

  fun crosswalk() = setOf(
    left(),
    right(),
    down(),
    up(),
  )

  fun diagonals() = setOf(
    right().up(),
    left().up(),
    right().down(),
    left().down()
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

interface Link {
  val point: Point
}

class Head : Link {
  override var point = Point(0, 0); private set

  fun step(direction: String) {
    point = when (direction) {
      "L" -> point.left()
      "R" -> point.right()
      "U" -> point.up()
      "D" -> point.down()
      else -> error("Unknown direction $direction")
    }
  }
}

class Tail(private val head: Link) : Link {

  val visited = mutableSetOf(Point(0, 0))

  override var point = Point(0, 0)
    private set(value) {
      field = value
      visited += value
    }

  fun step() {
    if (point.isTouching(head.point)) {
      return
    }
    if (point.onSameVerticalOrHorizontal(head.point)) {
      point = point.crosswalk().nearestTo(head.point)
      return
    }
    point = point.diagonals().nearestTo(head.point)
  }
}

class Game(len: Int) {
  private val head = Head()
  private val tails = mutableListOf<Tail>()

  init {
    tails.add(Tail(head))
    // потому что один элемент это голова, а второй мы добавили перед циклом
    repeat(len - 2) {
      val prev = tails.last()
      val tail = Tail(prev)
      tails.add(tail)
    }
  }

  private fun step(direction: String) {
    head.step(direction)
    tails.forEach(Tail::step)
  }

  fun process(command: String) {
    val (direction, count) = command.split(" ")
    repeat(count.toInt()) {
      step(direction)
    }
  }

  fun solution(): Int {
    return tails.last().visited.size
  }
}

fun main() {
  val game = Game(10)
  ::main.javaClass
    .getResourceAsStream("/input")!!
    .bufferedReader()
    .forEachLine(game::process)
  println(game.solution())
}