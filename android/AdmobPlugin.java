package com.tealeaf.plugin.plugins;
import com.tealeaf.TeaLeaf;
import com.tealeaf.logger;
import com.tealeaf.plugin.IPlugin;
import com.tealeaf.EventQueue;
import com.tealeaf.event.*;

import android.os.Bundle;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;

public class AdmobPlugin implements IPlugin {
    
    private Activity mActivity;
    
    public void AdmobPlugin() {
    }

    public void onCreateApplication(Context applicationContext) {
    }


    public void onCreate(Activity activity, Bundle savedInstanceState) {   
        mActivity = activity;
    }    

    public void onResume() {
    }

    public void onStart() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroy() {
    }

    public void onNewIntent(Intent intent) {
    }


    public void onActivityResult(Integer request, Integer result, Intent data) {
    }

    public boolean consumeOnBackPressed() {
        return true;
    }

    public void onBackPressed() {
    }
    
    public void testNative(String jsonData) {    
        logger.log(TAG, "Trying to toast");        
        
        Toast.makeText(mActivity.getApplicationContext(), 
                               "Test", Toast.LENGTH_LONG).show();
    }
}
