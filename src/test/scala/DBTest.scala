import Model.LogEntry
import dao.LogTable
import db.DBConnection
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funsuite.AnyFunSuite
import slick.lifted.TableQuery
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

class DBTest extends AnyFunSuite with ScalaFutures {
  val dbConnection = new DBConnection
  val logTable = TableQuery[LogTable]

  val logEntryOne = LogEntry("18:25:22.266", " main", "INFO ", "com.knoldus.Problem3$", " Find kth element : 8")
  val logEntryTwo = LogEntry("18:25:22.264", " main", "INFO ", "com.knoldus.Problem3$", " desired result : 10")
  val logEntryThree = LogEntry("20:25:22.265", " main", "INFO ", "com.knoldus.Problem3$", " actual result :  24")

  test("insert in to database") {
    val actualOne = dbConnection.insertEntry(logEntryOne)
    val actualTwo = dbConnection.insertEntry(logEntryTwo)
    val actualThree = dbConnection.insertEntry(logEntryThree)
    val expectedOne = Await.result(actualOne, 5.seconds)
    val expectedTwo = Await.result(actualTwo, 5.seconds)
    val expectedThree = Await.result(actualThree, 5.seconds)
    assert(expectedOne > 0)
    assert(expectedTwo > 0)
    assert(expectedThree > 0)
  }

  test("search message in the database") {
    val actual = Vector(LogEntry("18:25:22.264", " main", "INFO ", "com.knoldus.Problem3$", " desired result : 10"))
    val expected = dbConnection.searchPrintMessage("desired").map(_.distinct)
    val result = Await.result(expected, 5.seconds)
    assert(actual === result)
  }

  test("search logs between the start time and end time") {
    val startTime = "18:25:22.264"
    val endTime = "18:25:22.266"

    val expected = Vector(
      LogEntry("18:25:22.266", " main", "INFO ", "com.knoldus.Problem3$", " Find kth element : 8"),
      LogEntry("18:25:22.264", " main", "INFO ", "com.knoldus.Problem3$", " desired result : 10")
    )
    val resultFuture = dbConnection.searchTimestamp(startTime, endTime)
    val result = Await.result(resultFuture, 5.seconds)
    assert(expected === result)
  }

  test("if the logger message does not exist in the database") {
    val actual = dbConnection.searchPrintMessage("shrasti").map(_.distinct)
    val expectedExceptionMessage = "No log messages found"

    val result = intercept[Exception] {
      Await.result(actual, 5.seconds)
    }
    assert(result.getMessage == expectedExceptionMessage)
  }

  test("if start time and end time does not exist in database"){
    val startTime = "234567"
    val endTime = "892738"
    val actual = dbConnection.searchTimestamp(startTime, endTime)
    val expectedExceptionMessage = "Invalid Time"

    val result = intercept[Exception]{
      Await.result(actual, 5.seconds)
    }
    assert(result.getMessage == expectedExceptionMessage)
  }
}
