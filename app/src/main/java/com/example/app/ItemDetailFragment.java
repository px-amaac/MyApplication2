package com.example.app;

import android.app.Activity;
import android.os.Bundle;
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

        image = (ImageView) rootView.findViewById(R.id.image);


        brand_name = (TextView) rootView.findViewById(R.id.brand_name);
        product_name = (TextView) rootView.findViewById(R.id.product_name);
        price = (TextView) rootView.findViewById(R.id.price);
        percentoff = (TextView) rootView.findViewById(R.id.percentoff);
        schedule_button = (Button) rootView.findViewById(R.id.schedule_button);

        String imagePath;
        imagePath = fragData.get("image");
        il.displayImage(imagePath, image, dio);
        String mbrand_name = fragData.get("brand_name");
        brand_name.setText(mbrand_name);
        String mproduct_name = fragData.get("product_name");
        product_name.setText(mproduct_name);

        String mprice = fragData.get("price");
        price.setText(mprice);
        String mpercentoff =  fragData.get("product_name");
        percentoff.setText(mpercentoff);
        fragData = gd.getData();
        schedule_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lanuch alarm service.
            }
        });
        return rootView;

    }
}

