import scala.io.Source

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers._

class LoadDataSpec extends AnyFreeSpec {
  val dataFile = "day_xx_test"

  "day1" ignore {
    "reads the test file and returns the parsed data" in {
      loadData.day1("test_data") mustEqual ???
    }
  }

  "day2" ignore {
    "reads the test file and returns the parsed data" in {
      loadData.day2("test_data") mustEqual ???
    }
  }

}
