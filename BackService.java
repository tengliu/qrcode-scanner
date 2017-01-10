package com.sduwh.qrcodeSacnner;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Hashtable;

/**
 * Author: 小康康
 */

public class BackService extends Service implements ShakeListener.OnShakeListener {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        }
    };

    public ServiceBinder mBinder = new ServiceBinder();
    public class ServiceBinder extends Binder{
        public BackService getService(){
            return BackService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //Toast.makeText(getApplicationContext(),"后台服务启动",Toast.LENGTH_SHORT);
        Log.i("a", "后台服务启动");
        ShakeListener shakeListener = new ShakeListener(this);//创建一个对象
        shakeListener.setOnShakeListener(this);
    }


    @Override
    public void onShake() {
        //摇晃时触发的事件
            Toast.makeText(getApplicationContext(),"摇晃了手机，启动截图",Toast.LENGTH_SHORT);
            Log.i("a","摇晃");

        Intent screenshot = new Intent(this, TakeScreenShotActivity.class);
        screenshot.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(screenshot);
    }


    private String imageFileName;

    // 保存 Bitmap 对象为截图文件
     private boolean saveBitmap2File(Bitmap bmp) throws IOException {
        String APPENDSYMBOL = "/";
        File childFolder = new File(Environment.getExternalStorageDirectory() + APPENDSYMBOL + Environment.DIRECTORY_DCIM  + APPENDSYMBOL + "Screenshots");

         // 产生图片名称和路径
        imageFileName = childFolder.getAbsolutePath() + APPENDSYMBOL + String.valueOf(System.currentTimeMillis()) + ".png";

         // 保存为 PNG 图片
        boolean result = save2PNG(bmp, imageFileName);// 输出至文件

        if(result){
            // 这里可以加入 MediaScanner 实施扫描代码



        }

        return result;
    }

    /**
     * 指定目录写入文件内容 PNG
     *
     * @param filePath
     * @throws IOException
     */
    public static boolean save2PNG(Bitmap bitmap, String filePath) throws IOException {
        FileOutputStream fos = null;

        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        } finally {
            if (fos != null) {
                fos.close();
            } else {
                return false;
            }
        }
        return true;
    }


    // 窗口管理器
    private WindowManager mWindowManager;

    // 屏幕截图（异步封装的内部方法）
    private AsyncTask<Void, Void, Boolean[]> mOrdinaryScreenShotTask; //异步实例
    public void OrdinaryScreenShotTaskPacked(){

        //检测异步任务同时运行
        if(mOrdinaryScreenShotTask != null
                && mOrdinaryScreenShotTask.getStatus() == AsyncTask.Status.RUNNING){
            return;
        }

        mOrdinaryScreenShotTask = new AsyncTask<Void, Void, Boolean[]>(){

            private int ScreenWidth;
            private int ScreenHeight;
            private int mScreenDensity;
            private MediaProjectionManager mMediaProjectionManager;
            private MediaProjection mMediaProjection;
            private VirtualDisplay mVirtualDisplay;
            private ImageReader mImageReader;

            // 创建 VirtualDisplay 对象
            private VirtualDisplay createVirtualDisplay() {
                Surface SURFACE = mImageReader.getSurface();
                if(SURFACE == null){
                    return null;
                }

                return mMediaProjection.createVirtualDisplay(BackService.class.getSimpleName(), ScreenWidth, ScreenHeight, mScreenDensity,
                                                             DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, SURFACE, null /* Callbacks */,
                                                             mHandler /* Handler */);
            }

            // 初始化环境
            private boolean createVirtualEnvironment() {
                if(mWindowManager == null){
                    return false;
                }

                getMediaProjectionManager();

                int[] ScreenSize = ScreenUtil.getScreenSize(mWindowManager);
                ScreenWidth = ScreenSize[0];
                ScreenHeight = ScreenSize[1];
                mScreenDensity = ScreenUtil.getDPI(mWindowManager);

                mImageReader = ImageReader.newInstance(ScreenWidth, ScreenHeight, PixelFormat.RGBA_8888, 2); // ImageFormat.RGB_565

                return true;
            }

            //获取 mMediaProjectionManager 对象
            private MediaProjectionManager getMediaProjectionManager(){
                if(mMediaProjectionManager == null){
                    mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(
                            Context.MEDIA_PROJECTION_SERVICE);
                }

                return mMediaProjectionManager;
            }

            // 播放截图声音
            private void playScreenShotSound() {
                AudioManager mAudioManager = ((AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE));
                int OriginalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                // int MaxVolume =
                // mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                // mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MaxVolume /
                // 2, AudioManager.FLAG_VIBRATE); //设置音量为震动

                if (OriginalVolume != 0) {
                    MediaPlayer mMediaPlayer = MediaPlayer.create(BackService.this,
                                                                  Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
                    mMediaPlayer.start();
                }
            }

            //预处理
            private void startVirtual() {
                if (mMediaProjection == null) {
                    // 获取 MediaProjection
                    mMediaProjection = mMediaProjectionManager.getMediaProjection(getScreenShotResultCode(), getScreenShotResultData());

                    mVirtualDisplay = createVirtualDisplay();
                } else {
                    mVirtualDisplay = createVirtualDisplay();
                }
            }

            //ScreenShot 屏幕截图结果包装
            private Boolean[] RESULTSTATUS = new Boolean[] { Boolean.valueOf(false),
                    Boolean.valueOf(false) }; // ScreenShot, Bitmap,

            boolean createVirtualEnvironment = false;

            @Override
            protected void onPreExecute() {
                //Toast.makeText(getApplicationContext(),"截图准备",Toast.LENGTH_SHORT);

                createVirtualEnvironment = createVirtualEnvironment();

                startVirtual();

                //播放截图声音
                Handler ScreenShotSound = new Handler();
                ScreenShotSound.postDelayed(new Runnable() {
                    public void run() {
                        playScreenShotSound();
                    }
                }, 300);

                super.onPreExecute();
            }

            //截图对象 Bitmap
            Bitmap savedBitmap;

            @Override
            protected Boolean[] doInBackground(Void... params) {
                if(!createVirtualEnvironment || mImageReader == null || mVirtualDisplay == null){
                    return null;
                }

                //截图延迟
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException E) {
                }

                Image image = mImageReader.acquireLatestImage();
                int width = image.getWidth();
                int height = image.getHeight();
                final Image.Plane[] planes = image.getPlanes();
                final ByteBuffer buffer = planes[0].getBuffer();
                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * width;
                Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                image.close();

                if (bitmap != null) { //截图对象判断分支
                    RESULTSTATUS[0] = Boolean.valueOf(true);
                    try {
                        if (saveBitmap2File(bitmap)) { //截图保存状态分支
                            RESULTSTATUS[1] = Boolean.valueOf(true);
                            savedBitmap = bitmap;
                            //截图保存成功，接下来要解析二维码
                            Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
                            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
                            RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
                            //将图片转换成二进制图片
                            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
                            //初始化解析对象
                            QRCodeReader reader = new QRCodeReader();
                            //开始解析
                            Result result1 = null;
                            try {
                                result1 = reader.decode(binaryBitmap, hints);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            //return result;
                            //获得了解析结果result1
                            Log.i("a",result1.getText());
                        } else {
                            RESULTSTATUS[1] = Boolean.valueOf(false);
                        }
                    } catch (IOException E) {
                        RESULTSTATUS[1] = Boolean.valueOf(false);
                    }
                } else {
                    RESULTSTATUS = null;
                }

                return RESULTSTATUS;
            }

            @Override
            protected void onPostExecute(Boolean[] result) {
                super.onPostExecute(result);


                if (result == null) {


                    Toast.makeText(getApplicationContext(),"截图失败",Toast.LENGTH_SHORT);

                    tearDownMediaProjection();
                    return;
                }

                if (!result[1]) {

                    Toast.makeText(getApplicationContext(),"截图保存时失败",Toast.LENGTH_SHORT);

                    tearDownMediaProjection();
                    return;
                }

                Toast.makeText(getApplicationContext(),"截图成功了！",Toast.LENGTH_SHORT);

                tearDownMediaProjection();

                // 回收截图 Bitmap 对象
                if(savedBitmap != null){
                    savedBitmap.recycle();
                }
            }

            private void tearDownMediaProjection() {
                if (mMediaProjection != null) {
                    mMediaProjection.stop();
                    mMediaProjection = null;
                }

                stopVirtualDisplay();

                setScreenShotResultData(null);
            }

            private  void stopVirtualDisplay() {
                if (mVirtualDisplay == null) {
                    return;
                }
                mVirtualDisplay.release();
                mVirtualDisplay = null;
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static int mScreenShotResultCode;
    private static Intent mScreenShotResultData;

    public void setScreenShotResultCode(int RESULT_CODE){
        mScreenShotResultCode = RESULT_CODE;
    }
    public void setScreenShotResultData(Intent RESULT_DATA){
        mScreenShotResultData = RESULT_DATA;
    }

    public int getScreenShotResultCode(){
        return mScreenShotResultCode;
    }
    public Intent getScreenShotResultData(){
        return mScreenShotResultData;
    }

}
