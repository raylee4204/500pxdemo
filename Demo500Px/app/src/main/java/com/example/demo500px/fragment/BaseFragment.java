package com.example.demo500px.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Kanghee
 */
public class BaseFragment extends Fragment{

    protected AppCompatActivity getSupportActivity() {
        return (AppCompatActivity) getActivity();
    }
}
