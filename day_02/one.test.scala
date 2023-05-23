import scala.io.Source

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._

// class MathSuite extends munit.FunSuite {
//   test("addition") {}
// }

class OneSpec extends AnyFreeSpec {
  val dataFile = "day_xx_test"
  def testData = loadData.day1(dataFile)

  "run" ignore {
    "test" in {
      val result = one.run(testData)

      result mustEqual -1
    }
  }

}
