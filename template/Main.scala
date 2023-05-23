import scala.io.Source

import template._

object Main extends App {
  val dataFile = "day_xx_puzzle"

  println("Part one: " + one.run(loadData.day1("puzzle_data")))
  println("Part two: " + two.run(loadData.day2("puzzle_data")))

}
