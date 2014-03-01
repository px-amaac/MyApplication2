package com.example.app;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ShaDynastys on 2/24/14.
 */
public class ZapposJSONParser {

    public List<HashMap<String, String>> readStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readInput(reader);
        }finally{
                reader.close();
        }
    }

    public List<HashMap<String, String>> readInput(JsonReader reader) throws IOException {


        List<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> currentTotal = new HashMap<String, String>();
        String statusCode = null;
        Integer currentResultsCount = null;
        Integer totalResultsCount = null;
        Integer limit = null;

        reader.beginObject();

        if(reader.nextName().equals("limit")){
            limit = reader.nextInt();
        }
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
                    currentTotal.put("currentResultsCount", reader.nextString());

                }else if (name.equals("totalResultsCount")){
                    currentTotal.put("totalResultsCount", reader.nextString());
                }else{
                    reader.skipValue();
                }
            }
            reader.endObject();
            if(!currentTotal.isEmpty())
                items.add(currentTotal);
            return items;
        }
        else
            return null;
    }

    public HashMap<String, String> readItems(JsonReader reader) throws IOException{

        final String[] names = {"price", "originalPrice", "productUrl", "productName", "brandName", "thumbnailImageUrl", "percentOff", "productId"};
        HashMap<String, String> data = new HashMap <String, String>();

        reader.beginObject();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if(name.equals(names[0])){
                data.put(names[0], reader.nextString());
            } else if(name.equals(names[1])){
                data.put(names[1], reader.nextString());
            }else if(name.equals(names[2])){
                data.put(names[2], reader.nextString());
            }else if(name.equals(names[3])){
                data.put(names[3], reader.nextString());
            }else if(name.equals(names[4])){
                data.put(names[4], reader.nextString());
            }else if(name.equals(names[5])){
                data.put(names[5], reader.nextString());
            }else if(name.equals(names[6])){
                data.put(names[6], reader.nextString());
            }else if(name.equals(names[7])){
                data.put(names[7], reader.nextString());
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return data;
    }
}
