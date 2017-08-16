package com.yefeiw.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by yefeiw on 7/3/17.
 */
@JsonInclude
@Data
@Entity
public class Ad implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final int MAX_NUM_PRICE = 15;
    @Id
    public String asin;

    public String title; // required
    //Note here, it will be a list of price instead of one price
    public double price; // required
    public double discount;
    public String thumbnail; // required
    public String description; // required
    public String brand; // required
    public String detail_url; // required
    public String category;
    //date of the last query;
    public long date;
    //mandatory fields.
    private static final String[] mandatories = {"adId","asin","title","detail_url","price","category"};

    public Ad() {

    }

    public Ad(JSONObject object) {
        //mandatories
        asin = object.getString("asin");
        title = object.getString("title");
        detail_url = object.getString("detail_url");
        category = object.getString("category");
        //optionals
        thumbnail = object.has("thumbnail") ? object.getString("thumbnail") : "";
        description = object.has("description") ? object.getString("description") : "";
        brand = object.has("brand") ? object.getString("brand") : "";
        discount = object.has("discount") ? object.getDouble("discount") : 0;
        price = object.has("price") ? object.getDouble("price") : new Random().nextInt(1000) / 10.0;
    }

    //static method for external callers to check if the JSON object is valid
    //if not, don't create the object
    public static boolean isValid(JSONObject object) {
        //check for mandatories
        for (String key : mandatories) {
           if (!object.has(key)) {
               return false;
           }
        }
        return true;
    }
    //write method, have to be synchronized
    public synchronized void update(Double price) {
        this.price = price;
        //update recent timestamp
        Date date = new Date();
        this.date = date.getTime();
    }
}
