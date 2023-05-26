## Distributed Logging System Using Kafka
##### Introduction
This repository contains the code for a Kafka-based system.The system includes a producer that writes log data to a Kafka topic and a consumer that consumes log data from the Kafka topic, performs data processing, and stores it in a database.

##### Prerequisites
To run the Kafka System , you need to have the following prerequisites installed -<br>
ScalaVersion(2.11.12)<br>
SBT version(1.8.2)<br>
Apache Kafka <br>
Mysql(8.0.33)

##### Components
Producer - The Producer class is responsible for reading log data from a file and writing it to a specified Kafka topic. It uses the Kafka Producer API to send log data as key-value pairs.<br>
Consumer - The Consumer class consumes log data from a Kafka topic, performs data processing, and stores it in a database. It utilizes the Kafka Consumer API to subscribe to the topic and receive log records.<br>
DBConnection - he DBConnection class manages the database connection and provides methods for inserting log entries into the database. It uses Slick, a modern database query and access library for Scala.<br>
LogEntry - The LogEntry class represents a log entry and contains various fields such as timestamp, thread, level, logger, and message. It is used to encapsulate log data before inserting it into the database.<br>

##### Installation
Step 1 -> Clone the project<br>
Step 2 -> sbt compile <br>
Step 3 -> sbt run<br>

