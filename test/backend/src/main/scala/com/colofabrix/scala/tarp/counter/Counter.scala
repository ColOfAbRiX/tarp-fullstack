package com.colofabrix.scala.tarp.counter

import monocle.syntax.all.*

case class Counter(id: Long, count: Long) {

  def addOne: Counter =
    this
      .focus(_.count)
      .modify(_ + 1)

}
