package com.yefeiw.domain.rabbitconsumer;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;

public class Consumer {

    private final ConnectionFactory connectionFactory;

    /**
     * The routing key is the message type that the consumer wants to read from the queue.
     */

    /**
     * The queue name is the name of the queue that is shared between a pool of
     * consumers for load balancing the messages within the same service.
     */
    private String queueName;


    public Consumer(String queueName, ConnectionFactory connectionFactory, Receiver receiver) {
        this.queueName = queueName;
        this.connectionFactory = connectionFactory;

        initContainer(receiver);
    }


    public String getQueueName() {
        return queueName;
    }


    private void initContainer(Receiver receiver) {
        // set up the queue, exchange, binding on the broker
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        Queue queue = new Queue(queueName);
        admin.declareQueue(queue);
        TopicExchange exchange = new TopicExchange("data-distribution");
        admin.declareExchange(exchange);
        admin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(queueName));

        // set up the listener and container
        SimpleMessageListenerContainer container =
                new SimpleMessageListenerContainer(connectionFactory);

        MessageListenerAdapter adapter = new MessageListenerAdapter(receiver, "receiveMessage");
        container.setMessageListener(adapter);
        container.setQueueNames(queueName);
        container.start();
    }

}
