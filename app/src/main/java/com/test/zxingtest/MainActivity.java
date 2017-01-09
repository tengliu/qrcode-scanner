package com.test.zxingtest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.avos.avoscloud.feedback.*;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends Activity {
    private String TAG = "Service";
    private int result = 0;
    private Intent intent = null;
    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private Switch aSwitch = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    // private boolean enableService=false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent1=new Intent(getApplicationContext(), ThreadActivity.class);
//        startActivity(intent1);
//        FeedbackAgent agent = new FeedbackAgent(this);
//        agent.startDefaultThreadActivity();
        aSwitch = (Switch) findViewById(R.id.switch1);
        Button feedBackButton=(Button)findViewById(R.id.feedBack);
        feedBackButton.setOnClickListener(new feedBackListener());
        //获取用户是否同意后台服务
        SharedPreferences settings = getSharedPreferences("enableService", 0);
        if (settings.getBoolean("enableService", false) == false) {
            Intent intent = new Intent(getApplicationContext(), BackService.class);
            stopService(intent);
            aSwitch.setChecked(false);
        } else {
            Intent intent = new Intent(getApplicationContext(), BackService.class);
            startService(intent);
            aSwitch.setChecked(true);
        }
        //this.setVisible(false);


        aSwitch.setOnCheckedChangeListener(new switchServiceListener());
        mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
        //Service1.mMediaProjectionManager1 = mMediaProjectionManager;
        //((ShotApplication)getApplication()).setMediaProjectionManager(mMediaProjectionManager);
        //Log.i(TAG, "start screen capture intent");

        //finish();


//        startIntent();
//
//        Intent intent = new Intent(getApplicationContext(), BackService.class);
//        intent.setAction("com.test.zxingtest.BackService");
//        intent.setPackage("com.test.zxingtest");
//        this.startService(intent);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startIntent() {
        if (intent != null && result != 0) {
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            ((ShotApplication) getApplication()).setResult(result);
            ((ShotApplication) getApplication()).setIntent(intent);
            //Intent intent = new Intent(getApplicationContext(), Service1.class);
            //startService(intent);
            //Log.i(TAG, "start service Service1");
        } else {
            //startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            //Service1.mMediaProjectionManager1 = mMediaProjectionManager;
            ((ShotApplication) getApplication()).setMediaProjectionManager(mMediaProjectionManager);
            Log.i(TAG, "user agree the application to capture screen");
            //Service1.mResultCode = resultCode;
            //Service1.mResultData = data;
            //result = resultCode;
            //intent = data;
            //((ShotApplication)getApplication()).setResult(resultCode);
            //((ShotApplication)getApplication()).setIntent(data);
            Intent intent = new Intent(getApplicationContext(), BackService.class);
            //startService(intent);
            Log.i(TAG, "start service Service1");
            Toast.makeText(getApplicationContext(), "摇晃屏幕，即可解析屏幕上的二维码！（微信不支持哦）", Toast.LENGTH_LONG);
            //MainActivity.this.finish();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
    private class feedBackListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(getApplicationContext(),ThreadActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private class switchServiceListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences settings = getSharedPreferences("enableService", 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("enableService", isChecked);
            editor.commit();
            //enableService=isChecked;
            if (isChecked == false) {
                Intent intent = new Intent(getApplicationContext(), BackService.class);
                getApplication().stopService(intent);
                Log.i("a", "关闭后台服务");
                Toast.makeText(getApplicationContext(), "您已关闭摇一摇功能", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getApplicationContext(), BackService.class);
                startService(intent);
                Toast.makeText(getApplicationContext(), "摇晃手机，即可解析屏幕上二维码！", Toast.LENGTH_SHORT).show();
            }
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
