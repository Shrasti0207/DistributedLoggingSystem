package dao
import Model.LogEntry
import slick.jdbc.MySQLProfile.api._

class LogTable(tag: Tag) extends Table[LogEntry](tag,"Catalog"){
  // Define columns and their data types
  def timestamp: Rep[String] = column[String]("timestamp")

  def thread: Rep[String] = column[String]("thread")

  def level: Rep[String] = column[String]("level")

  def logger: Rep[String] = column[String]("logger")

  def message: Rep[String] = column[String]("message")
  // mapping between the table columns and case class LogEntry
  def * = (timestamp, thread, level, logger, message) <> (LogEntry.tupled, LogEntry.unapply)
}


