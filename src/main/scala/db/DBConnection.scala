package db
import Model.LogEntry
import dao.LogTable
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DBConnection() {

  val db = Database.forConfig("mysql")
  val logtable = TableQuery[LogTable]
  db.run(logtable.schema.create)

  //this method inserts the log entry in the database
  def insertEntry(entry: LogEntry): scala.concurrent.Future[Int] = {
    val insert = logtable += entry
    db.run(insert)
  }

  // This function searches for log messages containing a specific text and prints each matching element.
  def searchPrintMessage(message: String): Future[Seq[LogTable#TableElementType]] = {
    val query = logtable.filter(_.message.like(s"%$message%"))
    val resultFuture: Future[Seq[LogTable#TableElementType]] = db.run(query.result)
    resultFuture.flatMap { result =>
      if (result.isEmpty) {
        val exceptionMessage = s"No log messages found"
        Future.failed(new Exception(exceptionMessage))
      } else {
        result.foreach { element =>
          println(element)
        }
        Future.successful(result)
      }
    }
  }

  // This function searches for log messages within a specified timestamp range and prints each matching element.
  def searchTimestamp(startTime: String, endTime: String): Future[Seq[LogTable#TableElementType]] = {
    val query = logtable.filter(logTable => logTable.timestamp >= startTime && logTable.timestamp <= endTime)
    val resultFuture: Future[Seq[LogTable#TableElementType]] = db.run(query.result)
    resultFuture.flatMap { result =>
      if (result.isEmpty) {
        val exceptionMessage = s"Invalid Time"
        Future.failed(new Exception(exceptionMessage))
      } else {
        result.foreach { element =>
          println(element)
        }
        Future.successful(result)
      }
    }
  }
}
