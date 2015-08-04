package com.example.demo500px.activity;

import com.example.demo500px.R;
import com.example.demo500px.fragment.MainFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionSet;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Window window = getWindow();
        TransitionSet
                transitionSet = new TransitionSet().addTransition(new ChangeImageTransform())
                .addTransition(new ChangeBounds());
        window.setSharedElementEnterTransition(transitionSet);
        window.setSharedElementExitTransition(transitionSet);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, new MainFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getSupportFragmentManager().findFragmentById(R.id.fragment).onActivityResult(requestCode, resultCode, data);
    }
}
