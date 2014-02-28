package com.example.app.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCreator {

    /**
     * An array of sample (dummy) items.
     */
    private static List<HashMap<String, String> > ITEMS = null;
    private static final int listcount = 10;


    private final String[] keys = { "styleId", "price", "originalPrice", "productUrl", "colorId", "productName", "brandName", "thumbnailImageUrl", "percentOff", "productId"};
    private final String[] values = {"2276730", "$199.95", "$199.95", "http://www.zappos.com/product/8149427/color/401", "401", "Mutiny", "DC",
                "http://www.zappos.com/images/z2/2/7/6/7/3/2276730-t-THUMBNAIL.jpg", "0%", "8149427"};


    public DummyContentCreator(){
        ITEMS = new ArrayList<HashMap<String, String>>();
    }

    public List<HashMap<String, String>> generateItems(){
        while (ITEMS.size() < listcount){
            ITEMS.add(createItem());
        }
        return ITEMS;
    }

    private HashMap<String, String> createItem(){
        HashMap<String, String> result = new HashMap<String, String>();

        for(int i = 0; i < keys.length; i++) {
            result.put(keys[i], values[i]);
        }
        return result;
    }

}
