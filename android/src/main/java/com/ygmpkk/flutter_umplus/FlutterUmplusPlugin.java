package com.ygmpkk.flutter_umplus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.commonsdk.statistics.common.DeviceConfig;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import java.io.File;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.ActivityResultListener;

/**
 * FlutterUmplusPlugin
 */
public class FlutterUmplusPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware, ActivityResultListener {
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
        }else if (call.method.equals("isPlatInstall")) {
          isPlatInstall(call, result);
        }
        else if (call.method.equals("shareWeb")) {
            shareWeb(call, result);
        } else if (call.method.equals("shareImageText")) {
            shareImageText(call, result);
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

  private void isPlatInstall(MethodCall call, Result result) {
    String plat = call.argument("plat");
    boolean b=UMShareAPI.get(activity).isInstall(activity,getPlat(plat));
    result.success(b);
  }

  private void shareImageText(MethodCall call, Result result) {
        String plat = call.argument("plat");
        String text = call.argument("text");
        String icon = call.argument("icon");
        String image = call.argument("image");

        UMImage thumb = null;
        UMImage img = null;
        if (icon != null && !TextUtils.isEmpty(icon)) {
            thumb = new UMImage(activity, icon);
        }
        if (image != null && !TextUtils.isEmpty(image)) {
            if (image.startsWith("http")) {
                img = new UMImage(activity, image);
            } else {
                img = new UMImage(activity, new File(image));
            }
        }
        if (thumb != null && img != null) {
            img.setThumb(thumb);
        }
        new ShareAction(activity).setPlatform(getPlat(plat)).withText(text).withMedia(img).setCallback(new UmengshareActionListener(result)).share();
    }

    private void shareWeb(MethodCall call, Result result) {
        String plat = call.argument("plat");
        String title = call.argument("title");
        String desc = call.argument("desc");
        String icon = call.argument("icon");
        String webUrl = call.argument("webUrl");
        UMImage thumb = null;
        if (icon != null && !TextUtils.isEmpty(icon)) {
            thumb = new UMImage(activity, icon);
        }
        UMWeb web = new UMWeb(webUrl);
        web.setTitle(title);//标题
        if (thumb != null) {
            web.setThumb(thumb);//缩略图
        }
        web.setDescription(desc);//描述
        new ShareAction(activity)
                .setPlatform(getPlat(plat))
                .withMedia(web)
                .setCallback(new UmengshareActionListener(result))
                .share();
    }

    private SHARE_MEDIA getPlat(String plat) {
        if (plat == null) {
            return null;
        }
        switch (plat.toLowerCase()) {
            case "qq":
                return SHARE_MEDIA.QQ;
            case "q_zone":
                return SHARE_MEDIA.QZONE;
            case "wx":
                return SHARE_MEDIA.WEIXIN;
            case "wx_circle":
                return SHARE_MEDIA.WEIXIN_CIRCLE;
            case "sina":
                return SHARE_MEDIA.SINA;
        }
        return null;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }


    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        binding.addActivityResultListener(this);
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
        PlatformConfig.setWeixin(wxAppKey, wxAppSecret);
        PlatformConfig.setSinaWeibo(wbAppKey, wbAppSecret, null);
        PlatformConfig.setQQZone(qqAppID, qqAppKey);
        PlatformConfig.setQQFileProvider(activity.getPackageName()+".fileprovider");
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

    @Override
    public boolean onActivityResult(int i, int i1, Intent intent) {
        UMShareAPI.get(activity).onActivityResult(i, i1, intent);
        return false;
    }


}
