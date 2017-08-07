package com.yefeiw.domain.rabbitconsumer;

import com.yefeiw.domain.Ad;
import com.yefeiw.domain.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ReliableReceiver implements Receiver {

    //External Logger
    private Logger logger = Logger.getLogger("ReliableReceiver");
    private String consumerName;
    private AdRepository repository;


    public ReliableReceiver(String consumerName,AdRepository repository) {
        this.consumerName = consumerName;
        this.repository = repository;
    }

    @Override
    public void receiveMessage(byte[] message) throws Exception {
        logger.log(INFO,"[" + consumerName + "]  " + new String(message));
        //Check existense in the repository and update
        JSONObject object = new JSONObject(new String(message));
        String asin = object.getString("asin");
        double price = object.getDouble("price");

        if (repository == null) {
            logger.warning("invalid repository");
            return;
        }
        try {
            Ad ad = repository.findAdByAsin(asin);
            if (ad == null) {
                logger.log(INFO, "Creating new record with id = " + asin);
                if (Ad.isValid(object)) {
                    ad = new Ad(object);
                } else {
                    //don't do anything, return
                    logger.warning("object not valid, discarding message");
                    return;
                }
            }
            ad.update(price);
            logger.info("discount updated as " + ad.getDiscount());
            repository.save(ad);
        }catch (Exception e) {
            logger.warning("Save to DB failed");
            e.printStackTrace();
        }
    }

}
