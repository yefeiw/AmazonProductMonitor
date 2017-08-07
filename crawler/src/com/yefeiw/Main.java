package com.yefeiw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
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
        //counter of output files and input number
        int fileNum = 0;
        String starter = "";
        try {
            while (starter != null) {
                //read next valid input line
                String input = queryHandler.getLine();
                if (input == null) {
                    throw (new EOFException("EOF reached"));
                }
                if(input.length()== 0 || input.trim().length() == 0) {
                    continue;
                }
                logger.info("input query is "+ input);
                String[] words = input.split(",");
                //let query handler clean up the keywords
                words[0] = queryHandler.tokenizeString(words[0]);
                //let the crawler process the ads
                crawler.getAmazonProds(words,results);
                logger.info("total ads get from this page is "+results.size());
                if(results.size() == 0) {
                    logger.warning("Input empty for keywords "+words[0].trim());
                    continue;
                }
                //write output to files
                try {
                    objectMapper.writeValue(new File("out/output_" + fileNum++ + ".json"), results);
                } catch (Exception e) {
                    logger.warning("File output error");
                    e.printStackTrace();
                }
                for(Ad result : results) {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(result);
                    try {
                       JSONObject payload = new JSONObject(jsonString);
                       handler.sendMessage("queue1",payload);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                results = new ArrayList<Ad>(1000);
            }
        }catch (EOFException e) {
            //No need to process EOF
            logger.info("Processing finished");
            return;
        }catch (Exception e) {
            logger.warning("Found exception in Main function");
            e.printStackTrace();
    }
    }
}
