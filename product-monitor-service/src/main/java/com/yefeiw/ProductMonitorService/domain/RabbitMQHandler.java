package com.yefeiw.ProductMonitorService.domain;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagrant on 7/24/17.
 */
public class RabbitMQHandler {

    private ConnectionFactory factory;
    private Connection connection;
    private List<Channel> channelList = new ArrayList<Channel>();

    public RabbitMQHandler() {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        try {
            connection = factory.newConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void sendMessage(String queueName, JSONObject object) {
//        try {
//            channel.queueDeclare(queueName, false, false, false, null);
//            channel.basicPublish("", queueName, null, object.toString().getBytes());
//            System.out.println(" [x] Sent '" + object.toString() + "'");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void spawnReceiver(String queueName) {
        try {
            Channel channel = connection.createChannel();
            channel.queueDeclare(queueName, false, false, false, null);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(" [x] Received '" + message + "'");
                }
            };
            channel.basicConsume(queueName, true, consumer);

            //add channel to the list for reference
            channelList.add(channel);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void finalize() {
        try {
            for(Channel c : channelList) {
                c.close();
            }
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
