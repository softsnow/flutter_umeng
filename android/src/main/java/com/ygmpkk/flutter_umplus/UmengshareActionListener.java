package com.ygmpkk.flutter_umplus;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import io.flutter.plugin.common.MethodChannel.Result;
public class UmengshareActionListener implements UMShareListener{

    private final Result result;
    public UmengshareActionListener( Result result){
        this.result=result;
    }
    @Override
    public void onStart(SHARE_MEDIA share_media) {
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("um_status","SUCCESS");
        result.success("success");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("um_status","ERROR");
//        map.put("um_msg",throwable.getMessage());
        result.success("fail");
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("um_status","CANCEL");
        result.success("cancel");
    }

}
