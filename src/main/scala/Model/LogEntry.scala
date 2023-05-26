package Model

//this class holds the timestamp , thread, level, logger and message for he loggers
case class LogEntry(timestamp: String, thread: String, level: String, logger: String, message: String)

