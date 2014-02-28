package com.example.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.List;
import java.util.Map;

/**
 * Created by ShaDynastys on 2/27/14.
 */
public class ImageLoaderListAdapter extends SimpleAdapter {
    private int mResource;
    private int[] mTo;
    private String[] mFrom;
    private List<? extends Map<String, ?>> mData;
    private DisplayImageOptions dio = null;
    private ImageLoader il;

    public ImageLoaderListAdapter(Context context,
                                  List<? extends Map<String, ?>> data, int resource, String[] from,
                                  int[] to) {
        super(context, data, resource, from, to);
        mResource = resource;
        mData = data;
        mTo = to;
        mFrom = from;
        il = ImageLoader.getInstance();
        dio = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory()
                .showStubImage(R.drawable.testimg)
                .showImageForEmptyUri(R.drawable.testimg) // resource or drawable
                .cacheOnDisc().displayer(new RoundedBitmapDisplayer(20))
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        v = createViewFromResourceEx(v, position, convertView, parent,
                mResource);

        return v;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);

        v = createViewFromResourceEx(v, position, convertView, parent,
                mResource);

        return v;
    }

    private View createViewFromResourceEx(View v, int position,
                                          View convertView, ViewGroup parent, int resource) {

        bindView(position, v);

        return v;
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = getViewBinder();
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the
                            // bottom of these
                            // ifs since a lot of views are TextViews (e.g.
                            // CheckBoxes).
                            setViewText((TextView) v, text);
                        } else {
                            throw new IllegalStateException(v.getClass()
                                    .getName()
                                    + " should be bound to a Boolean, not a "
                                    + (data == null ? "<unknown type>"
                                    : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the
                        // bottom of these
                        // ifs since a lot of views are TextViews (e.g.
                        // CheckBoxes).
                        setViewText((TextView) v, text);
                    } else if (v instanceof ImageView) {
                        if (data instanceof Integer) {
                            setViewImage((ImageView) v, (Integer) data);
                        } else if (data instanceof Bitmap) {
                            setViewImage((ImageView) v, (Bitmap) data);
                        } else if (text.contains("http://")) {
                            il.displayImage(text, (ImageView) v, dio);
                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(
                                v.getClass().getName()
                                        + " is not a "
                                        + " view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    private void setViewImage(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
