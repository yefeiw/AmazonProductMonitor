package com.yefeiw;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import netscape.javascript.JSObject;
import org.json.JSONObject;

/**
 * Created by vagrant on 7/24/17.
 */
public class RabbitMQHandler {

    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;

    public RabbitMQHandler() {
        factory = new ConnectionFactory();
        try {
            connection = factory.newConnection("localhost");
            channel = connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String queueName, JSONObject object) {
        try {
//            channel.queueDeclare(queueName, false, false, false, null);
            channel.basicPublish(0, "",queueName,null, object.toString().getBytes());
            System.out.println(" [x] Sent '" + object.toString() + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finalize() {
//        try {
//            channel.close();
//            connection.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
