Spring RabbitMQ
===============

This project contains an example for using Spring-Boot with RabbitMQ. The idea is to have a single exchange *E* with multiple queues *Q_1 ... Q_n* for multiple Routing Keys *K_1 ... K_m* (mind that the number of queues can be different from the number of routing keys). The example allows dynamic registration of message consumers / workers *W_1 ... W_p* such that:

* Workers only need to know the name of the exchange *E* and the routing key *K* they are interested in
* Messages tagged with a routing key *K* are distributed to all queues that are registered at the exchange for this routing key
* Arbitrary workers *W_1 ... W_i* can form a worker pool that is bound to a single queue *Q_j*
* A message is processed by exactly one worker registered at a single queue
* Workers can be added and removed to a queue at runtime with no changes in configuration at the producer side



Prerequisites
-------------

* Install a local [RabbitMQ server](https://www.rabbitmq.com/install-standalone-mac.html)
  * RabbitMQ [config file](https://www.rabbitmq.com/configure.html) is expected in `/usr/local/etc/rabbitmq/rabbitmq.config` (go to Web UI to see the path on Windows machines)
* Start RabbitMQ with `rabbitmq-server`
  * RabbitMQ UI will be available at [http://localhost:15672/] afterwards



Building and Running the Application
------------------------------------

Either with

* Run `mvn package`
* Run `java -jar target/rabbitconsumer-0.0.1-SNAPSHOT.jar`

or simply

* `mvn spring-boot:run`

If you want to change the application multiple times with different ports, use `mvn spring-boot:run -Dserver.port=8090`



Using the Application
---------------------

The application provides two resources:

1. A message producer under [http://localhost:8080/producer](http://localhost:8080/producer)
1. A message consumer under [http://localhost:8080/consumer](http://localhost:8080/consumer)

### Registering a Consumer

For registering a new consumer, send a POST request to the following URL [localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}](localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey})

Corresponding CURL command:

    curl -X POST "http://localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}"

### Registering a "faulty Consumer"

A faulty consumer throws an exception instead of properly handling the message. You can register it by adding the `faultyConsumer=true` request parameter: [localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}?faultyConsumer=true](localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}?faultyConsumer=true)

### Registering a Long Running Consumer

A long running consumer takes a runtime as a parameter and sleeps for the given time after starting to process a message. We can thereby simulate what happens, if an instance of a consumer is busy. You can register it by adding the `runtime=30` request parameter: [localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}?faultyConsumer=true](localhost:8080/consumer/register/{consumerID}/{queueName}/{routingKey}?runtime=30)

### Sending POST Requests to Producer

This will send a new message to the queue. A message has a *messageType* which corresponds to the routing key and a *messageBody*.

For adding a message to the queue, send a POST request to the following URL [http://localhost:8080/producer](http://localhost:8080/producer).

Here's the "settings" for sending a POST request with Postman:

![Sending Post Request with Postman](/doc/screen-1.png?raw=true "Sending Post Request with Postman")

Corresponding CURL command:

    curl -X POST -H "Content-Type: application/json"-d '{"messageType" : "routingKey", "messageBody" : "Here comes the content"}' "http://localhost:8080/producer"

### Getting a Consumption Log

For getting a log of consumed messages, send a GET request to [http://localhost:8080/consumer](http://localhost:8080/consumer).

Corresponding CURL command:

    curl -X GET -H "Cache-Control: no-cache" -H "Postman-Token: 13678ff4-982e-1f5e-5842-00fdb2fe5d69" "http://localhost:8080/consumer"

The output looks like this:

    [
      "Started Logger",
      "2016/04/15 13:50:28 --- [consumer1]  dl 1",
      "2016/04/15 13:50:28 --- [consumer2]  dl 1",
      "2016/04/15 13:50:33 --- [consumer1]  dl 2",
      "2016/04/15 13:50:33 --- [consumer2]  dl 2",
      ...
    ]

The format is `"TIME --- [CONSUMER_ID]  MESSAGE_CONTENT"`



Usage Example
-------------

1. Start the application with `mvn spring-boot:run`
1. Register 4 consumers in 2 pools:
  * POST localhost:8080/consumer/register/pool1-consumer1/queue1/key1
  * POST localhost:8080/consumer/register/pool1-consumer2/queue1/key1
  * POST localhost:8080/consumer/register/pool2-consumer1/queue2/key1
  * POST localhost:8080/consumer/register/pool2-consumer2/queue2/key1
1. Send the following messages
  * POST http://localhost:8080/producer `{"messageType" : "key1", "messageBody" : "message 1"}`
  * POST http://localhost:8080/producer `{"messageType" : "key1", "messageBody" : "message 2"}`
  * POST http://localhost:8080/producer `{"messageType" : "key1", "messageBody" : "message 3"}`
  * POST http://localhost:8080/producer `{"messageType" : "key1", "messageBody" : "message 4"}`
1. Check the consumer log - this should result in

````
[
  "Started Logger",
  "2016/04/15 14:25:15 --- [pool1-consumer1]  message 1",
  "2016/04/15 14:25:15 --- [pool2-consumer1]  message 1",
  "2016/04/15 14:25:24 --- [pool2-consumer2]  message 2",
  "2016/04/15 14:25:24 --- [pool1-consumer2]  message 2",
  "2016/04/15 14:25:29 --- [pool1-consumer1]  message 3",
  "2016/04/15 14:25:29 --- [pool2-consumer1]  message 3",
  "2016/04/15 14:25:34 --- [pool1-consumer2]  message 4",
  "2016/04/15 14:25:34 --- [pool2-consumer2]  message 4"
]
````


Testing the Application
-----------------------

The application comes with a test suite that automatically tests all our assumptions concerning message handling. You can run the tests with

    mvn test

### Testing Asynchronous Behavior

The problem in our tests is that the message consumer consumers messages in an asynchronous manner. This means that we somehow have to "wait" until we can test the behavior of the consumer. Therefore we use `CountDownLatch` (following the idea of this [Stack Overflow post](http://stackoverflow.com/a/1829949/1549950)). Here's how it works:

First, we create a `CountDownLatch` object with the count of the messages that we want to send during our test (in this case, 2)

    CountDownLatch lock = new CountDownLatch(2);

Next we have to pass this lock to our consumer and count down 1 for each message, we consume:

    public void receiveMessage(String message) throws Exception {
        // ...
        lock.countDown();
    }

Back in our test, we have to wait until all expected messages are processed. We do this with a timeout (if something goes wrong so that we don't have to wait forever):

    lock.await(2000, TimeUnit.MILLISECONDS);

Afterwards we can run our assertions.



Further Resources
=================

* [Spring Initializr](https://start.spring.io/)
* [Spring RabbitMQ Sample Project](https://spring.io/guides/gs/messaging-rabbitmq/)
* [Spring AMQP Documentation](http://docs.spring.io/spring-amqp/reference/html/)
* [Spring AMQP Examples on Github](https://github.com/spring-projects/spring-amqp-samples)
* [Tutorial: Implementing REST Services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Postman App Download](https://www.getpostman.com/)


