package com.example.demo500px.fragment;

import com.example.demo500px.R;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment containing a grid of thumbnails
 */
public class MainFragment extends BaseFragment {

    private RecyclerView mRecyclerView;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        getSupportActivity().setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        getSupportActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.grid_thumbnails);
        int numColumns = getResources().getInteger(R.integer.num_columns_thumbnail);
        GridLayoutManager layoutManager = new GridLayoutManager(getSupportActivity(), numColumns);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }
}
