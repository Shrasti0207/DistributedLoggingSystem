import ProducerConsumer.kafka.{Consumer, Producer}
import db.DBConnection
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}

class KafkaSpec extends AnyFunSuite with EmbeddedKafka with BeforeAndAfterAll{
  val topicName = "logCollection-topic"
  val dbConnection = new DBConnection
  val producer = new Producer
  val consumer = new Consumer(dbConnection)
  implicit val config = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2181)

  override def beforeAll(): Unit = {
    EmbeddedKafka.start()
  }

  test("publish data into the kafka"){
    producer.writeToKafka(topicName)
    val response = consumeFirstStringMessageFrom(topicName)
    assert(Some(response).isDefined)
  }


  test("consume messages from Kafka") {
    val testData = Seq(
      "2023-05-15 23:30:24,469 | main | INFO |object_Oriented.Implicits$ |print the value of the number : 2",
      "2023-05-15 23:30:24,477 | main | INFO |object_Oriented.Implicits$ |print the value of the number : 3",
      "2023-05-15 23:30:24,478 | main | INFO |object_Oriented.Implicits$ |THE VALUE IS 42",
      "2023-05-16 17:55:43,370 | main | INFO |object_Oriented.Implicits$ |print the value of the number : 2",
      "2023-05-16 17:55:43,374 | main | INFO |object_Oriented.Implicits$ |print the value of the number : 3",
      "2023-05-16 17:55:43,375 | main | INFO |object_Oriented.Implicits$ |THE VALUE IS 42"
    )

    testData.foreach { message =>
      val producerRecord = new ProducerRecord[String, String](topicName, message)
      implicit val serializer: Serializer[String] = new StringSerializer
      publishToKafka(producerRecord)
    }
    val consumedMessages = consumer.consumeFromKafka(topicName)
    assert(consumedMessages == testData.length)
  }

  override def afterAll(): Unit = {
    EmbeddedKafka.stop()
  }
}
