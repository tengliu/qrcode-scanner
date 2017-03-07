package com.sduwh.qrcodeSacnner;

import android.app.Activity;
import android.content.Intent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
/**
 * Created by liuteng on 2017/1/17.
 */

public class xpMain implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if(!loadPackageParam.packageName.equals("com.sduwh.qrcodeScanner"))
        {return;}
        XposedBridge.log("二维码快扫启动");
        findAndHookMethod("com.sduwh.qrcodeScanner.ShakeListener", loadPackageParam.classLoader, "onShake", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Activity mCurrentActivity = (Activity) param.getResult();
                Intent intent=new Intent(mCurrentActivity,TakeScreenShotActivity.class);
                mCurrentActivity.startActivity(intent);
            }
        });


    }
}
