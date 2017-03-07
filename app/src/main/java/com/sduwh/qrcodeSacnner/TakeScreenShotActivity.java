package com.sduwh.qrcodeSacnner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class TakeScreenShotActivity extends AppCompatActivity {

    private MediaProjectionManager mMediaProjectionManager;
    private static final int TAKE_SCREEN_SHOT_REQUEST_CODE = 1069;

    //绑定并获得服务的实例
    private BackService mService;
    protected ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder iBinder) {
            mService = ((BackService.ServiceBinder) iBinder).getService();
        }
    };

    private Intent serviceIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        serviceIntent = new Intent(this, BackService.class);
        startService(serviceIntent); //启动服务

        getMediaProjectionManager();

        //询问用户截图吗？
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(),
                               TAKE_SCREEN_SHOT_REQUEST_CODE);

    }

    //获得 MediaProjectionManager 对象
    private MediaProjectionManager getMediaProjectionManager(){
        if(mMediaProjectionManager == null){
            mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }

        return mMediaProjectionManager;
    }

    public int RESULT_CODE;
    public Intent RESULT_DATA;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode){
            default:
                break;

            case TAKE_SCREEN_SHOT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    RESULT_CODE = resultCode;
                    RESULT_DATA = data;
                    Toast.makeText(getApplicationContext(),"正在进行二维码解析",Toast.LENGTH_SHORT).show();
                    new AsyncTask<Void, Void, Void>(){

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            //设置内容
                            mService.setScreenShotResultCode(RESULT_CODE);
                            mService.setScreenShotResultData(RESULT_DATA);
                        };

                        @Override
                        protected Void doInBackground(Void... params) {
                            finish();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            mService.OrdinaryScreenShotTaskPacked();

                        };

                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }else if (resultCode == RESULT_CANCELED) {
                   // Looper.prepare();
                    Toast.makeText(getApplicationContext(), "截图请求被取消", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE); //绑定服务
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection); //解绑服务
        super.onDestroy();
    }
}
