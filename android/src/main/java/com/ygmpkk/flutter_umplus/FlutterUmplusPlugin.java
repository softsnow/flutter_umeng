package com.ygmpkk.flutter_umplus;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterUmplusPlugin
 */
public class FlutterUmplusPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {
    private Activity activity;
    private MethodChannel channel;


    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "ygmpkk/flutter_umplus");
        channel.setMethodCallHandler(this);

    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("init")) {
            initSetup(call, result);
        } else if (call.method.equals("beginPageView")) {
            beginPageView(call, result);
        } else if (call.method.equals("endPageView")) {
            endPageView(call, result);
        } else if (call.method.equals("logPageView")) {
            logPageView(call, result);
        } else if (call.method.equals("event")) {
            event(call, result);
        } else {
            result.notImplemented();
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {

    }

    private static String getMetadata(Context context, String name) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getString(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        return null;
    }


    private void initSetup(MethodCall call, Result result) {
        String appKey = (String) call.argument("key");
        String channel = (String) call.argument("channel");
        String wxAppKey = (String) call.argument("wxAppKey");
        String wxAppSecret = (String) call.argument("wxAppSecret");
        String qqAppID = (String) call.argument("qqAppID");
        String qqAppKey = (String) call.argument("qqAppKey");
        String wbAppKey = (String) call.argument("wbAppKey");
        String wbAppSecret = (String) call.argument("wbAppSecret");
        Boolean logEnable = (Boolean) call.argument("logEnable");
        Boolean encrypt = (Boolean) call.argument("encrypt");
        Boolean reportCrash = (Boolean) call.argument("reportCrash");

        Log.d("UM", "initSetup: " + appKey);
//    Log.d("UM", "channel: " +  getMetadata(activity, "INSTALL_CHANNEL"));

        UMConfigure.setLogEnabled(logEnable);
        UMConfigure.init(activity, appKey, channel, UMConfigure.DEVICE_TYPE_PHONE,
                null);
        UMConfigure.setEncryptEnabled(encrypt);

        MobclickAgent.setSessionContinueMillis(30000L);
        MobclickAgent.setCatchUncaughtExceptions(reportCrash);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 大于等于4.4选用AUTO页面采集模式
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        } else {
            MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.MANUAL);
        }


        result.success(true);
    }

    public void beginPageView(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        Log.d("UM", "beginPageView: " + name);
        MobclickAgent.onPageStart(name);
        MobclickAgent.onResume(activity);
        result.success(null);
    }

    public void endPageView(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        Log.d("UM", "endPageView: " + name);
        MobclickAgent.onPageEnd(name);
        MobclickAgent.onPause(activity);
        result.success(null);
    }

    public void logPageView(MethodCall call, Result result) {
        // MobclickAgent.onProfileSignIn((String)call.argument("name"));
        // Session间隔时长,单位是毫秒，默认Session间隔时间是30秒,一般情况下不用修改此值
//    Long seconds = Double.valueOf(call.argument("seconds")).longValue();
//    MobclickAgent.setSessionContinueMillis(seconds);
        result.success(null);
    }

    public void event(MethodCall call, Result result) {
        String name = (String) call.argument("name");
        String label = (String) call.argument("label");
        if (label == null) {
            MobclickAgent.onEvent(activity, name);
        } else {
            MobclickAgent.onEvent(activity, name, label);
        }
        result.success(null);
    }

    public static String[] getTestDeviceInfo(Context context) {
        String[] deviceInfo = new String[2];
        try {
            if (context != null) {
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
                Log.d("UM", deviceInfo[0]);
                Log.d("UM", deviceInfo[1]);
            }
        } catch (Exception e) {
        }
        return deviceInfo;
    }


}
