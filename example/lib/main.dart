import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_umplus/flutter_umplus.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();

    initUMeng();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FlutterUmplus.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  initUMeng() {
    // channel 可设置为空
    FlutterUmplus.init(
      'Your umeng appkey',
      channel: 'Your channel',
      reportCrash: false,
      logEnable: true,
      encrypt: true,
    );

    FlutterUmplus.beginPageView('demo');
    FlutterUmplus.endPageView('demo');
    FlutterUmplus.event('eventName', label: 'eventLabel');
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('FlutterUmengPlugin-share Push'),
        ),
        body: new ListView(
          padding: EdgeInsets.only(top: 10),
          children: <Widget>[
            new Center(
              child: RaisedButton(
                onPressed: () {
                  shareWebView();
                },
                child: Text("分享网页"),
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 其中 icon 需要配置在项目中
  Future<void> shareWebView() async {
    String result;
    try {
      result = await FlutterUmplus.share(title: '分享标题',desc:'分享简介',icon: 'AppIcon',webUrl: 'https://www.baidu.com' ,plat: 'qq');
    } on PlatformException {
      result = 'fail';
    }
    if (!mounted) return;
    setState(() {
      print(result);
    });
  }
}
