import 'dart:async';

import 'package:flutter/services.dart';

class FlutterUmplus {
  static const MethodChannel _channel = const MethodChannel('ygmpkk/flutter_umplus');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> init(
    String key, {
    String channel,
    String wxAppKey,
    String wxAppSecret,
    String qqAppID,
    String qqAppSecret,
    String wbAppKey,
    String wbAppSecret,
    String wbRedirectURL,
    bool reportCrash = true,
    bool encrypt = false,
    bool logEnable = false,
  }) {
    Map<String, dynamic> args = {"key": key, "channel": channel,"wxAppKey": wxAppKey, 'wxAppSecret':wxAppSecret, "qqAppID": qqAppID,
      "qqAppSecret": qqAppSecret,
      "wbAppKey": wbAppKey,
      "wbAppSecret": wbAppSecret,
      "wbRedirectURL": wbRedirectURL,};

    if (reportCrash != null) {
      args["reportCrash"] = reportCrash;
    }

    if (encrypt != null) {
      args["encrypt"] = encrypt;
    }

    if (logEnable != null) {
      args["logEnable"] = logEnable;
    }

    _channel.invokeMethod("init", args);
    return new Future.value(true);
  }
  static Future<String> share(
      {String plat,String title, String desc, String icon, String webUrl}) async {
    Map<String, dynamic> shareMap = {
      "plat":plat,
      "title": title,
      "desc": desc,
      "icon": icon,
      "webUrl": webUrl,
    };
    final String result = await _channel.invokeMethod('share', shareMap);
    return result;
  }

  /// 打开页面时进行统计
  /// [name]
  static Future<Null> beginPageView(String name) async {
    _channel.invokeMethod("beginPageView", {"name": name});
  }

  /// 关闭页面时结束统计
  /// [name]
  static Future<Null> endPageView(String name) async {
    _channel.invokeMethod("endPageView", {"name": name});
  }

  /// 登陆统计
  /// [id]
  /// [interval]
  static Future<Null> logPageView(String name, {int seconds}) async {
    _channel.invokeMethod("logPageView", {"name": name, seconds: seconds});
  }

  /// 计数事件统计
  /// [eventId]  当前统计的事件ID
  /// [label] 事件的标签属性
  static Future<Null> event(String name, {String label}) async {
    _channel.invokeMethod("event", {"name": name, "label": label});
  }
}
