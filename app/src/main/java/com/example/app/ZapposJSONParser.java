package com.example.app;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ShaDynastys on 2/24/14.
 */
public class ZapposJSONParser {

    public List<Item> readStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readInput(reader);
        }finally{
                reader.close();
        }
    }

    public List<Item> readInput(JsonReader reader) throws IOException {


        List<Item> items = new ArrayList<Item>();
        String statusCode = null;
        Integer currentResultsCount = null;
        Integer totalResultsCount = null;

        reader.beginObject();


        if(reader.nextName().equals("statusCode")){
            statusCode = reader.nextString();
        }
        if(statusCode.equals("200")){
            while(reader.hasNext())
            {
                String name = reader.nextName();
                 if(name.equals("results")){
                        reader.beginArray();
                        while(reader.hasNext()) {
                            items.add(readItems(reader));
                        }
                        reader.endArray();
                 }
                else if (name.equals("currentResultsCount")){
                    currentResultsCount = reader.nextInt();
                }else if (name.equals("totalResultsCount")){
                    totalResultsCount = reader.nextInt();
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();
            return items;
        }
        else
            return null;
    }

    public Item readItems(JsonReader reader) throws IOException{
        String price = null;
        String percentOff = null;
        String imgUrl = null;
        String productName = null;
        String brandName = null;
        String productId = null;

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals("price")){
                price = reader.nextString();
            } else if(name.equals("percentOff")){
                percentOff = reader.nextString();
            }else if(name.equals("imgUrl")){
                imgUrl = reader.nextString();
            }else if(name.equals("productName")){
                productName = reader.nextString();
            }else if(name.equals("brandName")){
                brandName = reader.nextString();
            }else if(name.equals("productId")){
                productId = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Item(price, percentOff, imgUrl, productName, brandName, productId);
    }
}
