package com.example.jennifertran.cse110practice;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by emd_000 on 11/7/2015.
 */
public class registrationIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        startService(new Intent(this, RegistrationIntentService.class));
    }
}
