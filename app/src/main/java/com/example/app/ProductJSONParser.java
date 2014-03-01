package com.example.app;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by ShaDynastys on 2/28/14.
 */
public class ProductJSONParser {

        public HashMap<String, String> readStream(InputStream in) throws IOException {
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            try {
                return readInput(reader);
            }finally{
                reader.close();
            }
        }

        public HashMap<String, String> readInput(JsonReader reader) throws IOException {
            HashMap<String, String> item = new HashMap<String, String>();

            String statusCode;


            reader.beginObject();

            while(reader.hasNext()){

                String tmp = reader.nextName();
                if (tmp.equals("statusCode")){
                    statusCode =reader.nextString();

                    if(statusCode.equals("401"))
                        break;
                    item.put("statusCode", statusCode);
                }
                else if(tmp.equals("product")){
                    reader.beginArray();
                    while(reader.hasNext()) {
                        item = readItems(reader);
                    }
                    reader.endArray();
                }else
                    reader.skipValue();

            }
            reader.endObject();
            return item;
        }

    public HashMap<String, String> readItems(JsonReader reader) throws IOException{

                    HashMap<String, String> data = new HashMap <String, String>();
                    final String[] names = {"defaultProductUrl", "defaultImageUrl", "productId", "productName", "brandId", "brandName", "styles"};
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
                        }else if(name.equals(names[6])){ //styles
                            reader.beginArray();
                            while(reader.hasNext()) {
                                data.putAll(readStyles(reader));
                            }
                            reader.endArray();
                        }else
                            reader.skipValue();
                    }
                    reader.endObject();
                    return data;
                }

                public HashMap<String, String> readStyles(JsonReader reader) throws IOException{
                    HashMap<String, String> data = new HashMap <String, String>();
                    final String[] names = {"price", "originalPrice"};

                    reader.beginObject();

                    while(reader.hasNext()) {

                        String name = reader.nextName();

                        if(name.equals(names[0])){
                            data.put(names[0], reader.nextString());
                        } else if(name.equals(names[1])){
                            data.put(names[1], reader.nextString());
                        }
                        else{reader.skipValue();}


                    }
                    reader.endObject();
                    return data;

                }
            }



