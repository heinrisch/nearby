package com.sbla.nearby;


import android.app.Activity;
import android.os.Bundle;

/**
 * Has a single button, used to start the Wearable MainActivityMobile.
 */
public class MainActivityMobile extends Activity{

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}

