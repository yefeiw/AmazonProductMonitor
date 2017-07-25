package com.yefeiw;
import com.fasterxml.jackson.core.util.BufferRecycler;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by jiayangan on 6/24/17.
 */

public class Crawler {
    //String constants
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private final String authUser = "bittiger";
    private final String authPassword = "cs504";
    private static final String AMAZON_QUERY_URL = "https://www.amazon.com/s/ref=nb_sb_noss?field-keywords=";
    private static final String AMAZON_DP_URL = "https://www.amazon.com/dp/";
    private static final Logger logger = Logger.getLogger("crawler");
    private static final String PROXY_FILENAME = "proxy.txt";
    //List of proxy addresses. Reserved for
    List<String> proxies;
    Random random;

    private void getProxies() {
      QueryHandler handler = new QueryHandler(PROXY_FILENAME);
      proxies = handler.getLines();
      random = new Random();
    }

    public void initProxy() {
        getProxies();
        System.setProperty("http.proxyHost", "199.101.97.159"); // set proxy server
        System.setProperty("http.proxyPort", "60099"); // set proxy port
        //System.setProperty("http.proxyUser", authUser);
        //System.setProperty("http.proxyPassword", authPassword);
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                authUser, authPassword.toCharArray());
                    }
                }
        );
    }

    public void setRandomProxy() {
        //Sample proxy: 199.101.97.130,60099,61336,bittiger,cs504
        int line = random.nextInt(proxies.size()-1);
        try {
            String[] proxyLine = proxies.get(line).split(",");
            System.setProperty("http.proxyHost", proxyLine[0]); // set proxy server
            System.setProperty("http.proxyPort", proxyLine[1]); // set proxy port
            logger.info("Setting proxyHost to " + proxyLine[0]);
        } catch (Exception e) {
            logger.warning("Invalid proxy position "+ line + " out of"+proxies.size());
            e.printStackTrace();
        }
    }
    public void testProxy() {

        String test_url = "http://www.toolsvoid.com/what-is-my-ip-address";
        try {
            HashMap<String,String> headers = new HashMap<String,String>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate");
            //headers.put("Accept-Language", "en-US,en;q=0.8");
            Document doc = Jsoup.connect(test_url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();
            String iP = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong").first().text(); //get used IP.
            System.out.println("IP-Address: " + iP);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void getAmazonProds(String[] input, List<Ad> ads) {
        //variables
        String query = input[0].replace(' ', '+'); //text of the query
        String bid = input[1].trim();
        String campaignId = input[2].trim();
        String queryGroupId = input[3].trim();
        //paging handling
        int pageLimit = 1;//initial value of the total pages, will update in the first page.
        //HashSet to detect duplicate
        HashSet<String> asinSet = new HashSet<String>();

        try {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Language", "en-US,en;q=0.8");
            setRandomProxy();
            for (int pageNum = 1; pageNum <= pageLimit; pageNum++) {
                String url = AMAZON_QUERY_URL + query + "&page=" + pageNum;
                logger.info("url   " + url);
                Document doc = Jsoup.connect(url).maxBodySize(0).headers(headers).userAgent(USER_AGENT).timeout(10000).get();
                Thread.sleep(100);
                logger.info("doc size is" + doc.body().text().length());
                Elements prods = doc.getElementsByClass("s-result-item celwidget ");
                //DOM
                if (prods.size() == 0) {
                    logger.info("No products for query "+query);
                    if (doc.body().text().contains("not a robot")) {
                        logger. warning("banned by robots.txt, re-running query");
                        setRandomProxy();
                        //reset page number and re-run search
                        pageNum --;
                        continue;
                    }
                    //no products here, return
                    return;

                }
                //#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4
                Element category = doc.select("#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4").first();
                String categoryStr = category.text();
                System.out.println("prod category: " + categoryStr);
                //caution: set base
                String resultCount = doc.getElementById("s-result-count").text();
                logger.info(resultCount);
                int index = resultCount.indexOf("-");
                int base = 0;
                if (index != -1) {
                    //otherwise, means it's less than a page, set base to 0
                    base = Integer.parseInt(resultCount.substring(0,index))-1;
                }

                logger.info(" base for this page is "+ base) ;
                logger.info("number of prod: " + prods.size());
                for (Integer i = base; i < base+prods.size(); i++) {
                    Ad ad = new Ad();
                    try {
                        String id = "result_" + new Integer(i).toString();
                        Element prodsById = doc.getElementById(id);
                        String asin = prodsById.attr("data-asin");
                        //detect duplicates
                        if (asinSet.contains(asin)) {
                            logger.info("duplicate detected, continuing");
                            continue;
                        } else {
                            asinSet.add(asin);
                        }

                        //init ad fields
                        ad.campaignId = Integer.parseInt(campaignId);
                        ad.bidPrice = Double.parseDouble(bid);
                        ad.adId = Integer.parseInt(queryGroupId);
                        ad.category = categoryStr;
                        ad.query = input[0];//original query;
                        Elements titleEleList = prodsById.getElementsByAttribute("title");
                        ad.title = titleEleList.attr("title");
                        ad.keyWords = Arrays.asList(query.split(" "));
                        try {
                            Element description = doc.select("#result_" + Integer.toString(i) + "  > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span5.a-span-last > div:nth-child(1) > span.a-size-small.a-color-secondary.a-text-bold").first();
                            if (description != null) {
                                Element descText = doc.select("#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span5.a-span-last > div:nth-child(1) > span:nth-child(3)").first();
                                ad.description = descText.text();
                                logger.info("Description: "+ad.description);
                            }
                        } catch (Exception e) {
                            logger.info("No description for asin"+asin);
                        }
                        //furnish fields
                        ad.detail_url = AMAZON_DP_URL + asin;
                        Element brand = doc.select("#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div > span:nth-child(2)").first();
                        ad.brand = (brand != null) ? brand.text() : "";
                        String price = prodsById.getElementsByClass("a-color-base sx-zero-spacing").attr("aria-label");
                        //caution: need to remove dollar sign
                        price = price.replace('$',' ');
                        price = price.replace(',',' ');
                        price = price.replaceAll(" ","");

                        //caution: no prices
                        if (price.isEmpty()) {
                            logger.info("No price for product "+asin);
                            ad.price = 0;

                        } else {
                            if (price.contains("-")) {
                                //if there is a price range.... guess the median of range as price
                                String[] priceRange = price.split("-");
                                ad.price = (Double.parseDouble(priceRange[1]) + Double.parseDouble(priceRange[0])) / 2.0;
                            } else {
                                ad.price = Double.parseDouble(price);
                            }
                        }
                        Element thumbnail = doc.select("#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img").first();
                        if (thumbnail == null) {
                            logger.warning("Product " + asin + " does not have thumbnail");
                            ad.thumbnail = "";
                        } else {
                            ad.thumbnail = thumbnail.attr("src");
                        }



                        //add the new ad to the results
                        ads.add(ad);
                    } catch (NullPointerException e) {
                        logger.warning("product not found, continuing");
                        e.printStackTrace();
                    }
                }
                if (pageNum == 1) {
                    try {
                        //two possibilities: pageLimit is the last grey link or the last blue link
                        Elements greys = (doc.getElementsByClass("pagnDisabled"));
                        if (greys != null && greys.size() > 0) {
                            //Since we are on the first page, the last grey page must be the limit
                            pageLimit = Integer.parseInt(greys.last().text());
                        } else {
                            pageLimit = Integer.parseInt(doc.getElementsByClass("pagnLink").last().text());
                        }
                        logger.info("Page limit is "+pageLimit);
                        //For speed, change to 3 pages max
                        pageLimit = Math.min(pageLimit,3);
                    }catch (Exception e) {
                        logger.info("query "+query+" only has one page");
                        pageLimit = 1;
                    }


                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void parseAmazonProdPage(String url) {
        try {
            HashMap<String,String> headers = new HashMap<String,String>();
            headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "en-US,en;q=0.8");
            Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();
            Element titleEle = doc.getElementById("productTitle");
            String title = titleEle.text();
            System.out.println("title: " + title);

            Element priceEle =doc.getElementById("priceblock_ourprice");
            String price = priceEle.text();
            System.out.println("price: " + price);

            //review
            //#cm-cr-dp-review-list
            Elements reviews = doc.getElementsByClass("a-expander-content a-expander-partial-collapse-content");
            System.out.println("number of reviews: " + reviews.size());
            for (Element review : reviews) {
                System.out.println("review content: " + review.text());
            }

            //#customer_review-R188VC0CBW8NLR > div:nth-child(4) > span > div > div.a-expander-content.a-expander-partial-collapse-content


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
