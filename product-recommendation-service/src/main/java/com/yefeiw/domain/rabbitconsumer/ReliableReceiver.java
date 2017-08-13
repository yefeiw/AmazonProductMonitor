package com.yefeiw.domain.rabbitconsumer;

import com.yefeiw.domain.Ad;
import com.yefeiw.domain.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ReliableReceiver implements Receiver {

    //External Logger
    private Logger logger = Logger.getLogger("ReliableReceiver");
    private String consumerName;
    private AdRepository repository;
    private RedisTemplate redisTemplate;


    public ReliableReceiver(String consumerName,AdRepository repository, RedisTemplate redisTemplate) {
        this.consumerName = consumerName;
        this.repository = repository;
        this.redisTemplate = redisTemplate;
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
        if (redisTemplate == null) {
            logger.warning("invalid redis template");
            return;
        }
        try {
            ValueOperations<String,Ad> operations = redisTemplate.opsForValue();
            if(!redisTemplate.hasKey(asin)) {
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
                operations.set(asin,ad,1, TimeUnit.MINUTES);
            }
            else {
                Ad ad = operations.get(asin);
                logger.info("Found duplicate at product " +ad.title);
            }
        }catch (Exception e) {
            logger.warning("Save to DB failed");
            e.printStackTrace();
        }
    }

}
