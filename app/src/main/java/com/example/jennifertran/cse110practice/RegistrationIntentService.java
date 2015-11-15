package com.example.jennifertran.cse110practice;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by emd_000 on 11/7/2015.
 */
public class RegistrationIntentService extends IntentService {
    public static final String[] TOPICS = {"global"};
    private String username;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RegistrationIntentService(String name) {
        super(name);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        username = intent.getStringExtra("username");
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            //gcm_defaultSenderId is pulled from google.json which lives under app/
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            sendRegistrationToServer(token);
            subscribeTopics(token);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void sendRegistrationToServer(String token)
    {
        Map<String,String> params = new HashMap<>();
        params.put("auth","qwepoi12332191827364");

        //Sql queries quizes for a particular user. The sql database names the user
        // specific list of headers and quizzes: usernameQuizzes
        params.put("query", "INSERT INTO tokens (token) VALUES ("+token+")");
        JSONParser p = new JSONParser();
        JSONArray j = p.makeHttpRequest(getApplicationContext().getString(R.string.queryUrl),
                "POST", params);

    }

    private void subscribeTopics(String token) throws IOException{
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for(String topic : TOPICS)
            pubSub.subscribe(token,"/topics/"+topic,null);
    }
}
