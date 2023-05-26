package ProducerConsumer.kafka
import Model.LogEntry
import db.DBConnection
import org.apache.kafka.clients.consumer.ConsumerConfig._
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.{IntegerDeserializer, StringDeserializer}
import java.time.Duration
import scala.collection.JavaConverters._
import java.util.Properties
import java.util.concurrent.{Executors, Future}
import scala.concurrent.{ExecutionContext}

object PrivateExcecutionContext{
  val executor = Executors.newFixedThreadPool(5)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executor)
}

class Consumer(dbConnection: DBConnection){
  import PrivateExcecutionContext._
  //this method consumes a log messages from a kafka topic and inserts them into the database
  def consumeFromKafka(topic: String): Unit = {
    val consumerProperties = new Properties()

    //Set the kafka consumer properties
    consumerProperties.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
    consumerProperties.setProperty(GROUP_ID_CONFIG, "group-id-1")
    consumerProperties.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest")
    consumerProperties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, classOf[IntegerDeserializer].getName)
    consumerProperties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    consumerProperties.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false")

    val consumer = new KafkaConsumer[Int, String](consumerProperties)
    consumer.subscribe(List(topic).asJava)      //subscribe to the specific topic

    var consumedMessages = 0
    while (true) {
      val polledRecords: ConsumerRecords[Int, String] = consumer.poll(Duration.ofSeconds(1))
      if (!polledRecords.isEmpty) {
        println(s"Polled ${polledRecords.count()} records")
        val recordIterator = polledRecords.iterator()
        while (recordIterator.hasNext) {
          val record = recordIterator.next()
          val value = record.value()
          val logData = value.trim.stripMargin
          val lines = logData.split("\n")
          for (line <- lines) {
            val segments = line.split("\\|")
            if (segments.length == 5) {
              val timestamp = segments(0).trim
              val thread = segments(1).trim
              val level = segments(2).trim
              val logger = segments(3).trim
              val message = segments(4).trim
              val logEntry = LogEntry(timestamp, thread, level, logger, message)
              consumedMessages += 1
              dbConnection.insertEntry(logEntry)        //insert the entries into the database
            }
          }
        }
      }
      consumer.commitSync(Duration.ofSeconds(1))
    }
  }
}

object ConsumerMain extends App{
  val dbConnection = new DBConnection
  val consumer = new Consumer(dbConnection)
  val topicname = "logCollection-topic"
  consumer.consumeFromKafka(topicname)
}