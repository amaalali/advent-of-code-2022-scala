#!/usr/bin/env -S scala-cli shebang

//> using toolkit "latest"
//> using lib "com.lihaoyi::pprint:0.8.1"

import scala.util.matching.Regex.Match
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import sttp.client4.Request
import sttp.client4.Response
import sttp.client4.quick.*
import sttp.model.MediaType

/** Part 0: Helpers
  */

def snakeCase(xs: String*) = xs.mkString("_")

/** PART 1: Parse input
  */

// Parse day value
val CONST_VALID_DAY_FORMAT = """^([1-9]|0[1-9]|1\d|2[0-5])$""".r
val CONST_VALID_DAY_ERROR_MESSAGE = "Day must be between 1 and 25"

val (
  iNPUT_DAY_AS_FOLDER_NAME,
  iNPUT_DAY_AS_INT
): (String, Int) =
  args.toList.headOption
    .toRight("Program needs a day number. " + CONST_VALID_DAY_ERROR_MESSAGE)
    .map(CONST_VALID_DAY_FORMAT.findFirstMatchIn)
    .flatMap(
      _.toRight("Invalid input for day. " + CONST_VALID_DAY_ERROR_MESSAGE)
    )
    .fold(
      err => {
        pprint.pprintln(s"[ERROR] ${err}")
        sys.exit(1)
      },
      value => {
        val INPUT_DAY = value.matched
        val INPUT_DAY_ZERO_PADDED = if (INPUT_DAY.length() == 1) s"0${INPUT_DAY}" else INPUT_DAY
        (snakeCase("day", INPUT_DAY_ZERO_PADDED), INPUT_DAY.toInt)
      }
    )

// Parse year value
val INPUT_YEAR = "2022"

/** Part 2: Retreive session Token
  */
val CONST_TOKEN_ENV_KEY = "AOC_TOKEN"

val getSessionToken =
  scala.util.Properties
    .envOrNone(CONST_TOKEN_ENV_KEY)
    .map(envValue =>
      envValue.split("=").toList match
        case key :: value :: _ => key -> value
        case _ =>
          pprint.pprintln(s"[ERROR] Invalid format for AOC Token. Must be in the format `session=<token-value>`")
          sys.exit(1)
    ) match
    case None =>
      pprint.pprintln(s"[ERROR] Missing environment variable for AOC Token. Token ${CONST_TOKEN_ENV_KEY} must be set and be formatted as `session=<token-value>`")
      sys.exit(1)

    case Some(value) => value

/** Part 3: Constants needed later in programme
  */

val CONST_PUZZLE_URI = uri"https://adventofcode.com/${INPUT_YEAR}/day/${iNPUT_DAY_AS_INT}/input"

val CONST_PROJECT_ROOT_DIR = os.pwd
val CONST_TEMPLATE_SOURCE_DIR = CONST_PROJECT_ROOT_DIR / "template"

val CONST_DESTINATION_DATA_DIR = CONST_PROJECT_ROOT_DIR / "data"
val CONST_DATA_FILE_PUZZLE = CONST_DESTINATION_DATA_DIR / snakeCase(iNPUT_DAY_AS_FOLDER_NAME, "puzzle")
val CONST_DATA_FILE_TEST = CONST_DESTINATION_DATA_DIR / snakeCase(iNPUT_DAY_AS_FOLDER_NAME, "test")

val CONST_DESTINATION_WORKSPACE_DIR = CONST_PROJECT_ROOT_DIR / iNPUT_DAY_AS_FOLDER_NAME

/** PART 4: Validate project with input value
  */

// Check if there is a directory with name
if (os.exists(CONST_DESTINATION_WORKSPACE_DIR)) {
  pprint.pprintln(s"[ERROR] Invalid name provided. There is already a folder at ${CONST_DESTINATION_WORKSPACE_DIR}")
  sys.exit(1)
}

// validate that there is a template folder
if (!os.exists(CONST_TEMPLATE_SOURCE_DIR)) {
  pprint.pprintln(
    s"[ERROR] Could not find the template directory `${CONST_TEMPLATE_SOURCE_DIR}`." +
      " " +
      "Hint: The working directory must be the same as script and template"
  )
  sys.exit(1)
}

if (os.exists(CONST_DATA_FILE_PUZZLE)) {
  pprint.pprintln(s"[ERROR] There is already file ${CONST_DATA_FILE_PUZZLE}")
  sys.exit(1)
}

if (os.exists(CONST_DATA_FILE_TEST)) {
  pprint.pprintln(s"[ERROR] There is already file ${CONST_DATA_FILE_TEST}")
  sys.exit(1)
}

/** PART 5: Cleanup - Remove scala-cli folders and files from template folder if they exist
  */
os.remove.all(CONST_TEMPLATE_SOURCE_DIR / ".scala-build")
os.remove.all(CONST_TEMPLATE_SOURCE_DIR / ".bsp")

/** PART 6: Do work
  */

// Create directories
os.makeDir(CONST_DESTINATION_WORKSPACE_DIR)
pprint.pprintln(s"[INFO] Created folder ${CONST_DESTINATION_WORKSPACE_DIR} using template ${CONST_TEMPLATE_SOURCE_DIR}")

// Copy over files
for (sourceFilePath <- os.list.stream(CONST_TEMPLATE_SOURCE_DIR)) {
  os.copy.into(sourceFilePath, CONST_DESTINATION_WORKSPACE_DIR)
  pprint.pprintln(s"[INFO] Created file ${sourceFilePath}")
}

// Fetch puzzle data and write to file
try {
  val request =
    quickRequest
      .get(CONST_PUZZLE_URI)
      .cookie(getSessionToken)
      .acceptEncoding(MediaType.TextPlain.toString)

  val response: Response[String] =
    request
      .send()

  os.write.over(
    target = CONST_DATA_FILE_PUZZLE,
    data = response.body
  )
  pprint.pprintln(s"[INFO] Fetched puzzle data and cached in file ${CONST_DATA_FILE_PUZZLE}")
} catch {
  case e => pprint.pprintln(s"[ERROR] Error when creating ${CONST_DATA_FILE_PUZZLE}, ${e.getMessage()}")
}

// Create empty test data file
try {
  os.write(CONST_DATA_FILE_TEST, "")
  pprint.pprintln(s"[INFO] Created empty file for test puzzle input at ${CONST_DATA_FILE_TEST}")
} catch {
  case e => pprint.pprintln(s"[ERROR] Error when creating ${CONST_DATA_FILE_TEST}, ${e.getMessage()}")
}

pprint.pprintln("[INFO] Successfuly completed. Happy coding :)")
