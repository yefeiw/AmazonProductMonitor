package com.yefeiw.ProductMonitorService.rest;

import com.sun.net.httpserver.HttpsConfigurator;
import com.yefeiw.ProductMonitorService.services.ProductMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vagrant on 7/25/17.
 */
@RestController
public class ProductMonitorRestController {
    private ProductMonitorService productMonitorService;
    private Logger logger = LoggerFactory.getLogger(ProductMonitorRestController.class);

    @Autowired
    public ProductMonitorRestController(ProductMonitorService service) {
        System.out.println("Restcontroller setup");
        this.productMonitorService = service;
    }
    @RequestMapping(value = "/register/{category}", method = RequestMethod.POST)
    HttpStatus postCategory(@PathVariable(value = "category") String category) {
        logger.info("Registering "+category);
        productMonitorService.postCategory(category);
        return HttpStatus.CREATED;
    }

    @RequestMapping(value = "/recommend/{userid}", method = RequestMethod.GET)
    HttpStatus recommendForUser(@PathVariable(value = "userid") String userid) {
        logger.info("sending recommendation email for " + userid);
        productMonitorService.recommendForUser(userid);
        return HttpStatus.ACCEPTED;
    }

    @RequestMapping(value = "/process",method = RequestMethod.POST)
    HttpStatus process() {
        logger.info("crawler sent signal to process all queues");
        productMonitorService.process();
        return HttpStatus.CREATED;
    }

}
