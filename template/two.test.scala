import scala.io.Source

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._

class TwoSpec extends AnyFreeSpec {
  val dataFile = "day_xx_test"
  def testData = loadData.day2(dataFile)

  "run" ignore {
    "test" in {
      val result = two.run(testData)

      result mustEqual -1
    }
  }

}
