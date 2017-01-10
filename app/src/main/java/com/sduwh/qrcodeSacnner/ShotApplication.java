package com.sduwh.qrcodeSacnner;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;

import com.avos.avoscloud.AVOSCloud;


public class ShotApplication extends Application {
    private int result;
    private Intent intent;
    private MediaProjectionManager mMediaProjectionManager;
    private SharedPreferences sharedPreferences;
    @Override
    public void onCreate() {
        AVOSCloud.initialize(this, "2GBuCiDCqgLoeinv07LrUxWW-gzGzoHsz", "tIiw9lvVLImKENJ0VjGj3B3u");
        //根据用户设置决定是否自启动服务
        sharedPreferences=this.getSharedPreferences("enableService",0);
        Intent intent=new Intent(this,BackService.class);
        if (sharedPreferences.getBoolean("enableService",false)==true){
        startService(intent);
        }
        else {
            stopService(intent);
        }

    }

    public int getResult(){
        return result;
    }

    public Intent getIntent(){
        return intent;
    }

    public MediaProjectionManager getMediaProjectionManager(){
        return mMediaProjectionManager;
    }

    public void setResult(int result1){
        this.result = result1;
    }

    public void setIntent(Intent intent1){
        this.intent = intent1;
    }

    public void setMediaProjectionManager(MediaProjectionManager mMediaProjectionManager){
        this.mMediaProjectionManager = mMediaProjectionManager;
    }
}
