#import "FlutterUmplusPlugin.h"
#import <UMCommon/MobClick.h>
#import <UMCommon/UMCommon.h>
#import <UMCommonLog/UMCommonLogHeaders.h>
//#import <UMErrorCatch/UMErrorCatch.h>

@implementation FlutterUmplusPlugin {
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar> *)registrar {
  FlutterMethodChannel *channel =
      [FlutterMethodChannel methodChannelWithName:@"ygmpkk/flutter_umplus"
                                  binaryMessenger:[registrar messenger]];
  FlutterUmplusPlugin *instance = [[FlutterUmplusPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall *)call
                  result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS "
        stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"init" isEqualToString:call.method]) {
    [self initSetup:call result:result];
  } else if ([@"logPageView" isEqualToString:call.method]) {
    [self logPageView:call result:result];
  } else if ([@"beginPageView" isEqualToString:call.method]) {
    [self beginPageView:call result:result];
  } else if ([@"endPageView" isEqualToString:call.method]) {
    [self endPageView:call result:result];
  } else if ([@"event" isEqualToString:call.method]) {
    [self event:call result:result];
    result(nil);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)initSetup:(FlutterMethodCall *)call result:(FlutterResult)result {

  NSString *appKey = call.arguments[@"key"];
  NSString *channel = call.arguments[@"channel"];
  NSString *wxAppKey = call.argument("wxAppKey");
  NSString *wxAppSecret =call.argument("wxAppSecret");
  NSString *qqAppID = call.argument("qqAppID");
  NSString *qqAppKey = call.argument("qqAppKey");
  NSString *wbAppKey = call.argument("wbAppKey");
  NSString *wbAppSecret = (String)call.argument("wbAppSecret");
  BOOL logEnable = [call.arguments[@"logEnable"] boolValue];
  BOOL encrypt = [call.arguments[@"encrypt"] boolValue];
  BOOL reportCrash = [call.arguments[@"reportCrash"] boolValue];

  [UMCommonLogManager setUpUMCommonLogManager];

  [UMConfigure setLogEnabled:YES];
  [UMConfigure setEncryptEnabled:encrypt];
  NSString *deviceID = [UMConfigure deviceIDForIntegration];
  NSLog(@"集成测试的deviceID:%@", deviceID);

  [UMConfigure initWithAppkey:appKey channel:channel];

  [MobClick setCrashReportEnabled:reportCrash];
  //设置微信的appKey和appSecret
  [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_WechatSession appKey:wxAppKey appSecret:wxAppSecret redirectURL:nil];
  //QQ端统一和网页端使用相同的APPKEY
  [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_QQ appKey:qqAppID  appSecret:qqAppKey redirectURL:nil];
  /* 设置新浪的appKey和appSecret */
  [[UMSocialManager defaultManager] setPlaform:UMSocialPlatformType_Sina appKey:wbAppKey  appSecret:wbAppSecret redirectURL:nil];
  //[UMErrorCatch initErrorCatch];
  result(nil);
}

- (void)shareImageText:(FlutterMethodCall *)call result:(FlutterResult)result {
   [UMSocialUIManager setPreDefinePlatforms:@[@(UMSocialPlatformType_WechatSession),@(UMSocialPlatformType_WechatTimeLine),@(UMSocialPlatformType_QQ),@(UMSocialPlatformType_Sina)]];
                    //显示分享面板
                    [UMSocialUIManager showShareMenuViewInWindowWithPlatformSelectionBlock:^(UMSocialPlatformType platformType, NSDictionary *userInfo) {
                        //创建分享消息对象
                        UMSocialMessageObject *messageObject = [UMSocialMessageObject messageObject];
                        //设置文本
                        messageObject.text =call.arguments[@"shareText"];
                        //创建图片内容对象
                        UMShareImageObject *shareObject = [[UMShareImageObject alloc] init];
                        //如果有缩略图，则设置缩略图
  //                      shareObject.thumbImage = [UIImage imageNamed:@"icon"];
                        [shareObject setShareImage:call.arguments[@"shareImage"]];
  //                    [shareObject setShareImageArray:<#(NSArray *)#>];//或者是数组，支持新浪微博
                        //分享消息对象设置分享内容对象
                        messageObject.shareObject = shareObject;
                        //调用分享接口
                        [[UMSocialManager defaultManager] shareToPlatform:platformType messageObject:messageObject currentViewController:nil completion:^(id data, NSError *error) {
                            if (error) {
                                result(@"fail");
                            }else{
                                result(@"success");
                            }
                        }];
                    }];
}

- (void)beginPageView:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];

  NSLog(@"beginPageView: %@", name);

  [MobClick beginLogPageView:name];

  result(nil);
}

- (void)endPageView:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];

  NSLog(@"endPageView: %@", name);

  [MobClick endLogPageView:name];
  result(nil);
}

- (void)logPageView:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];
  int seconds = [call.arguments[@"seconds"] intValue];

  NSLog(@"logPageView: %@", name);
  NSLog(@"logPageView: %d", seconds);

  [MobClick logPageView:name seconds:seconds];

  result(nil);
}

- (void)event:(FlutterMethodCall *)call result:(FlutterResult)result {
  NSString *name = call.arguments[@"name"];
  NSString *label = call.arguments[@"label"];

  NSLog(@"event name: %@", name);
  NSLog(@"event label: %@", name);

  // TODO add attributes

  [MobClick event:name label:label];

  result(nil);
}

@end
