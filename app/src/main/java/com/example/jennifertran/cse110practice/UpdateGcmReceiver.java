package com.example.jennifertran.cse110practice;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.gcm.GcmReceiver;


public class UpdateGcmReceiver extends GcmReceiver {

    /**
     * Created by emd_000 on 11/7/2015.
     */
    public class UpdateGcmListenerService extends GcmListenerService {
        @Override
        public void onMessageReceived(String from, Bundle data) {
            String message = data.getString("message");
            if (from.startsWith("/topics/")) {  //message received from topic
            } else {
                //normal downstream message
            }
        }
    }
}