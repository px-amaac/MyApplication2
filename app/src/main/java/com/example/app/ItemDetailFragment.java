package com.example.app;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private GetData gd;
    private DisplayImageOptions dio = null;
    private ImageLoader il;
    public static final String ARG_ITEM_ID = "item_id";
    private ImageView image;
    private TextView brand_name;
    private TextView product_name;
    private TextView price;
    private TextView percentoff;
    private TextView url;
    private Button schedule_button;
    private HashMap<String, String> fragData;
    private static final String PREFS_NAME = "useritems";
    private ArrayList<String> itemIds;
    private String productId;
    SharedPreferences pref;

    /*callback interface*/
    public interface GetData {
        public HashMap<String, String> getData();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            gd = (GetData) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement GetData");
        }
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);
        il = ImageLoader.getInstance();
        dio = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.testimg)
                .showImageForEmptyUri(R.drawable.testimg).cacheInMemory()
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(20))
                .build();
        fragData = gd.getData();

        image = (ImageView) rootView.findViewById(R.id.image);


        brand_name = (TextView) rootView.findViewById(R.id.brand_name);
        product_name = (TextView) rootView.findViewById(R.id.product_name);
        price = (TextView) rootView.findViewById(R.id.price);
        percentoff = (TextView) rootView.findViewById(R.id.percentoff);
        url = (TextView) rootView.findViewById(R.id.url);
        schedule_button = (Button) rootView.findViewById(R.id.schedule_button);

        String imagePath;
        imagePath = fragData.get("thumbnailImageUrl");
        il.displayImage(imagePath, image, dio);
        String mbrand_name = fragData.get("brandName");
        brand_name.setText(mbrand_name);
        String mproduct_name = fragData.get("productName");
        product_name.setText(mproduct_name);

        String mprice = fragData.get("price");
        price.setText(mprice);
        String mpercentoff =  fragData.get("percentOff");
        percentoff.setText(mpercentoff);
        String murl =  fragData.get("productUrl");
        url.setText(murl);

        if(!checkSharedPreferences()){
            schedule_button.setVisibility(View.VISIBLE);
            schedule_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemIds.isEmpty()){
                        itemIds = new ArrayList<String>();
                    }
                    itemIds.add(productId);

                    StringBuilder sb = new StringBuilder();
                    for (String s: itemIds) {
                        sb.append(s).append(",");
                    }
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("user_items", sb.toString());
                    editor.commit();
                    AlarmScheduleReciever.scheduleAlarms(getActivity());
                }
            });
        }
        else
            schedule_button.setVisibility(View.GONE);
        return rootView;
    }
    /*returns true if the current item id is equal to any of the already stored ids. returns false otherwise.*/
    private Boolean checkSharedPreferences(){
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        productId = fragData.get("productId");
        String user_items = pref.getString("user_items", null);
        if (user_items != null)
        {
            itemIds.addAll(Arrays.asList(user_items.split(",")));
            for (String s: itemIds) {
                if (s.equals(productId))
                    return true;
            }
        }
        return false;
    }
}

