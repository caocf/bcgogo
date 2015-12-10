//
//  TGHttpManager.h
//  TGYIFA
//
//  Created by James Yu on 14-5-13.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "TGJsonNetworkEngine.h"

typedef NS_ENUM(NSInteger, TGHTTPRequest_ApiID) {
    apiID_user_login            =   1,          //用户登录
    apiID_get_SMS_content,                      //获取短信内容
    apiID_send_SMS,                             //发送短信
    apiID_remindHandle,                         //预约或者保养更改为已处理
    apiID_accept_appoint,                       //接受预约单
    apiID_change_appoint_time,                  //更改预约单服务时间
    apiID_get_faultInfo_list,                   //故障列表
    apiID_get_remind_list,                      //保养列表
};

//url路由
typedef struct {
    TGHTTPRequest_ApiID                      apiId;
    __unsafe_unretained  NSString            *urlStr;
}TGApiIDDictItem;

@interface TGHttpManager : NSObject

{
    TGJsonNetworkEngine  *_jsonNetWorkEngine;
    
}

+ (TGHttpManager *)sharedInstance;

//保存每个请求的operation
@property (nonatomic, strong) NSMutableArray *operationQueues;

- (void)cancelRquestWithViewControllerClass:(NSString *)aViewControllerClass;
- (void)cancelAllRequest;

- (void)login:(NSString *)userName password:(NSString *)password success:(success)aSuccess error:(error)aEror viewControllerClass:(Class)aViewControllerClass;

@end
