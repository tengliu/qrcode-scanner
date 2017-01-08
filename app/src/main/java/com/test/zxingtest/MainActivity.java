package com.test.zxingtest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.setVisible(false);
        mMediaProjectionManager = (MediaProjectionManager)getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        //Service1.mMediaProjectionManager1 = mMediaProjectionManager;
        //((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        //Log.i(TAG, "start screen capture intent");

        //finish();



        startIntent();
        Intent intent = new Intent(getApplicationContext(), BackService.class);
        intent.setAction("com.test.zxingtest.BackService");
        intent.setPackage("com.test.zxingtest");
        this.startService(intent);



    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent(){
        if(intent != null && result != 0){
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            ((ShotApplication)getApplication()).setResult(result);
            ((ShotApplication)getApplication()).setIntent(intent);
            //Intent intent = new Intent(getApplicationContext(), Service1.class);
            //startService(intent);
            //Log.i(TAG, "start service Service1");
        }else{
            //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            //Service1.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            //result = resultCode;
            //intent = data;
            //((ShotApplication)getApplication()).setResult(resultCode);
            //((ShotApplication)getApplication()).setIntent(data);
            Intent intent = new Intent(getApplicationContext(), BackService.class);
            startService(intent);
            Log.i(TAG, "start service Service1");
            Toast.makeText(getApplicationContext(),"摇晃屏幕，即可解析屏幕上的二维码！（微信不支持哦）",Toast.LENGTH_LONG);
            MainActivity.this.finish();
        }
    }




//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            if (resultCode != Activity.RESULT_OK) {
//                return;
//            }else if(data != null && resultCode != 0){
//                Log.i(TAG, "user agree the application to capture screen");
//                //Service1.mResultCode = resultCode;
//                //Service1.mResultData = data;
//                result = resultCode;
//                intent = data;
//                ((ShotApplication)getApplication()).setResult(resultCode);
//                ((ShotApplication)getApplication()).setIntent(data);
//                Intent intent = new Intent(getApplicationContext(), BackService.class);
//                startService(intent);
//                Log.i(TAG, "start service Service1");
//
//                MainActivity.this.finish();
//            }
//        }
//    }
}
