package com.yefeiw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class TestRunnable implements  Runnable{
    @Override
    public void run() {
        // write your code here
        //final String INPUT_FILENAME = "rawQuery3.txt";

        final String INPUT_FILENAME = "unitTest.txt";
        Logger logger = Logger.getLogger("crawler Test");
        QueryHandler queryHandler = new QueryHandler(INPUT_FILENAME);
        Crawler crawler = new Crawler();
        ObjectMapper objectMapper = new ObjectMapper();
        RabbitMQHandler handler = new RabbitMQHandler();
        crawler.initProxy();
        List<Ad> results = new ArrayList<Ad>(1000);
        Set<String> categorySet = new HashSet<String>();
        //counter of output files and input number
        String starter = "";
        try {
            while (starter != null) {
                //read next valid input line
                String input = queryHandler.getLine();
                if (input == null) {
                    throw (new EOFException("EOF reached"));
                }
                if (input.length() == 0 || input.trim().length() == 0) {
                    continue;
                }
                logger.info("input query is " + input);
                String[] words = input.split(",");
                //let query handler clean up the keywords
                words[0] = queryHandler.tokenizeString(words[0]);
                //let the crawler process the ads
                crawler.getAmazonProds(words, results);
                logger.info("total ads get from this page is " + results.size());
                if (results.size() == 0) {
                    logger.warning("Input empty for keywords " + words[0].trim());
                    continue;
                }
                for (Ad result : results) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(result);
                    try {
                        //Two things to be done here:
                        //1. If not declared queue, call queue declaration
                        String key = "";
                        for (char c : result.category.toCharArray()) {
                            if (Character.isLetter(c) ||Character.isDigit(c))
                                key += c;
                        }
                        logger.info ("Executing query with key "+ key);
                        if (!categorySet.contains(key)) {
                            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(3 * 1000).build();
                            CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
                            HttpPost httpPost = new HttpPost("http://localhost:8080/consumer/register/"+key);
                            logger.info("before request");
                            HttpResponse response = client.execute(httpPost);
                            logger.info("after request");
                            if(!(response.getStatusLine().getStatusCode() == 200)){
                                logger.warning("queue registration unsuccessful with code " + response.getStatusLine().getStatusCode());
                            } else{
                                logger.info(result.category +" queue creation successful");
                                categorySet.add(key);
                            }
                            client.close();
                        }
                        JSONObject payload = new JSONObject(jsonString);
                        handler.sendMessage(result.category, payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                results = new ArrayList<Ad>(1000);
            }
        } catch (EOFException e) {
            //No need to process EOF
            logger.info("Processing finished");
            return;
        } catch (Exception e) {
            logger.warning("Found exception in Main function");
            e.printStackTrace();
        }
    }
}
