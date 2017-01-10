/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.sduwh.qrcodeSacnner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;



public class BootCompletedReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferences=context.getSharedPreferences("enableService",0);
        Intent intent1 = new Intent(context, BackService.class);
        //intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (sharedPreferences.getBoolean("enableService",false)==true){
        context.startService(intent1);
        }
        else {
            context.stopService(intent1);
        }
    }
}
