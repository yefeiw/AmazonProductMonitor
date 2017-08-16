package com.yefeiw.domain.rabbitconsumer;

import com.yefeiw.domain.AdRepository;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsumerBuilder {

    @Autowired
    ConnectionFactory connectionFactory;
    @Autowired
    AdRepository repository;

    private int runtime = 0;

    private Receiver injectedReceiver;

    public Consumer build(String queueName) {
        Receiver receiver;
        if (this.injectedReceiver != null) {
            receiver = this.injectedReceiver;
        } else {
            receiver = new ReliableReceiver(queueName,repository);
        }
        return new Consumer( queueName, connectionFactory, receiver);
    }


    public ConsumerBuilder withRuntime(int runtime) {
        this.runtime = runtime;
        return this;
    }

    public ConsumerBuilder withReceiver(Receiver receiver) {
        this.injectedReceiver = receiver;
        return this;
    }

}
