package com.example.app;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ShaDynastys on 2/28/14.
 */
public class NotifyService  extends IntentService {

    public NotifyService(){
        super("NotifyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

}
