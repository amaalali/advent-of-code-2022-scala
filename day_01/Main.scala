import scala.io.Source

object Main extends App {
  val dataFile = "day_01_puzzle"

  println("Part one: " + one.run(loadData.part1("puzzle_data")))
  println("Part two: " + two.run(loadData.part2("puzzle_data")))

}
