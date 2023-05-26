import scala.io.Source

// import template._

object Main extends App {
  val dataFile = "day_02_puzzle"

  println("Part one: " + one.run(loadData.part1("puzzle_data")))
  println("Part two: " + two.run(loadData.part2("puzzle_data")))

}
