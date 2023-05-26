package ProducerConsumer.kafka
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import scala.io.Source
import scala.util.{Failure, Success, Try}

class Producer{
  val logData = new LogData

  // this method produced the logdata in the kafka topic
  def writeToKafka(topic: String): Unit = {
    val path = logData.parseCsv("/home/knoldus/IdeaProjects/Scala_questions/logfile.log")

    val producerProperties = new Properties()   //set the kafka producer properties
    producerProperties.setProperty(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
    )
    producerProperties.setProperty(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[IntegerSerializer].getName
    )
    producerProperties.setProperty(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
    )

    val producer = new KafkaProducer[Int, String](producerProperties)
    producer.send(new ProducerRecord[Int, String](topic, 1, path))
    producer.flush()
  }
}

class LogData {

  // this method parsing of log data from a file
  def parseCsv(path: String): String = {
    val file = Try(Source.fromFile(path))
    val result = file match {
      case Success(f) =>
        val lines = f.getLines.toList
        f.close()
        val regex = """^(.*?) \[(.*?)\] (.*?)  (.*?) - (.*)$""".r
        val parsedLines = lines.collect {
          case regex(date, thread, level, logger, message) =>
            s" $date | $thread | $level |$logger |$message "
        }
        parsedLines.mkString("\n")
      case Failure(exception) => throw new Exception(exception)
    }
    result
  }
}

object ProducerMain extends App{
  val producer = new Producer
  val topicName = "logCollection-topic"
  producer.writeToKafka(topicName)
}
