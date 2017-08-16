package com.yefeiw.domain.rabbitconsumer;

import com.google.gson.Gson;
import com.yefeiw.GlobalConfig;
import com.yefeiw.domain.Ad;
import com.yefeiw.domain.AdRepository;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ReliableReceiver implements Receiver {

    //External Logger
    private Logger logger = Logger.getLogger("ReliableReceiver");
    private String consumerName;
    private AdRepository repository;
    private RedisTemplate redisTemplate;
    private RabbitTemplate rabbitTemplate;


    public ReliableReceiver(String consumerName, AdRepository repository, RedisTemplate redisTemplate, RabbitTemplate rabbitTemplate) {
        this.consumerName = consumerName;
        this.repository = repository;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void receiveMessage(byte[] message) throws Exception {
        logger.log(INFO, "[" + consumerName + "]  " + new String(message));
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
            ValueOperations<String, Ad> operations = redisTemplate.opsForValue();
            if (redisTemplate.hasKey(asin)) {
                Ad ad = operations.get(asin);
                long timestamp = object.getLong("date");
                long timeDiff = timestamp - ad.date;
                if (Math.abs(timeDiff) < 60 * 1000) {
                    logger.info("Found duplicate at product " + ad.asin);
                    return;
                } else {
                    logger.info("Found updated product" + ad.asin);
                }

            }

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
            double discount = ad.price - price;
            ad.discount = Math.max(0,discount);
            ad.update(price);
            logger.info("discount updated as " + ad.getDiscount());
            repository.save(ad);
            operations.set(asin, ad, 2, TimeUnit.SECONDS);
            if (ad.discount > 0) {
                Gson gson = new Gson();
                String jsonString = gson.toJson(ad);
                JSONObject payload = new JSONObject(jsonString);
                rabbitTemplate.convertAndSend("recommendation",jsonString.getBytes());
            }


        } catch (Exception e) {
            logger.warning("Save to DB failed");
            e.printStackTrace();
        }
    }

}
