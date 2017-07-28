package com.yefeiw.ProductMonitorService.services;

import com.yefeiw.ProductMonitorService.domain.RabbitMQHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vagrant on 7/26/17.
 */
@Service
public class ProductMonitorServiceImpl implements ProductMonitorService {


    private Set<String> set = new HashSet<String>();
    private RabbitMQHandler handler;
    private Logger logger;
    private Set<String> categories;

    public ProductMonitorServiceImpl(){
        set = new HashSet<>();
        this.handler = new RabbitMQHandler();
        categories = Collections.synchronizedSet(set);
        logger = LoggerFactory.getLogger(ProductMonitorServiceImpl.class);
    }


    @Override
    public void postCategory(String category) {
            if (!categories.contains(category)) {
                categories.add(category);
                handler.spawnReceiver(category);
            }
    }

    @Override
    public void recommendForUser(String uid) {
        //TBD: add support
        logger.warn("Recommendation support not yet added");
    }

    @Override
    public  void process() {
        for (String category : categories) {
            //receive RabbitMQ messages

        }
    }
}
