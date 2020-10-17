package com.example.stchat;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class ExampleService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ExampleService() {
        super("ExampleService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while(true){
            Log.d(TAG,"hello");
            try {
                sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
