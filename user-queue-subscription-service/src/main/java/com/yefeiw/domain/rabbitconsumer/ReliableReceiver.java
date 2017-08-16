package com.yefeiw.domain.rabbitconsumer;

import com.yefeiw.domain.Ad;
import com.yefeiw.domain.AdRepository;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ReliableReceiver implements Receiver {

    //External Logger
    private Logger logger = Logger.getLogger("ReliableReceiver");
    private String consumerName;
    private AdRepository repository;


    public ReliableReceiver(String consumerName, AdRepository repository) {
        this.consumerName = consumerName;
        this.repository = repository;
    }

    @Override
    public void receiveMessage(byte[] message) throws Exception {
        logger.log(INFO, "[" + consumerName + "]  " + new String(message));
        //Check existense in the repository and update
        JSONObject object = new JSONObject(new String(message));
        String asin = object.getString("asin");
        double price = object.getDouble("price");


        try {
            Ad ad = new Ad(object);
            repository.save(ad);

        } catch (Exception e) {
            logger.warning("Save to DB failed");
            e.printStackTrace();
        }
    }

}
