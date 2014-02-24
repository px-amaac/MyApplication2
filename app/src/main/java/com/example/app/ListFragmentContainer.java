package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Toast;

/**
 * Created by ShaDynastys on 2/23/14.
 */
public class ListFragmentContainer extends ListFragment implements AbsListView.OnScrollListener {

    private Footer vFooter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vFooter = new Footer(getActivity());
        getListView().addFooterView(vFooter);
    }

    public void loadingComplete(String message) {

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null)
            return null;
        if (savedInstanceState != null){

        }
        Toast.makeText(getActivity(), "setup pagination here", Toast.LENGTH_LONG).show();
        return inflater.inflate(R.layout.list_fragment_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void laodingIndicator( int rId ) {
        LayoutInflater inflate = (LayoutInflater) super.
    }
}
