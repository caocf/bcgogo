//
//  TGMessageRollingHandler.m
//  TGOBD
//
//  Created by James Yu on 14-3-17.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "TGMessageRollingHandler.h"
#import "TGMessageDBManager.h"
#import "TGHTTPRequestEngine.h"
#import "TGDataSingleton.h"
#import "TGComplexModel.h"
#import "TGMacro.h"
#import "TGAlertView.h"
#import "TGAppDelegate.h"
#import "TGDTCManagerViewController.h"

static TGMessageRollingHandler *_rollingManager = nil;
static NSTimer                 *_rollingTimer = nil;

@implementation TGMessageRollingHandler


+ (void)startRolling
{
    if (_rollingManager == nil) {
        _rollingManager = [[TGMessageRollingHandler alloc] init];
    }
    
    if (_rollingTimer == nil) {
        _rollingTimer = [NSTimer scheduledTimerWithTimeInterval:TIME_MESSAGE_ROLLING
                                                         target:_rollingManager
                                                       selector:@selector(getNewMessage)
                                                       userInfo:nil
                                                        repeats:YES];
        [_rollingTimer fire];
    }
}

+ (void)stopRolling
{
    if (_rollingTimer) {
        [_rollingTimer invalidate];
    }
    _rollingTimer = nil;
}

- (void)getNewMessage
{
    TGDataSingleton *dataSingleton = [TGDataSingleton sharedInstance];
    
    [[TGHTTPRequestEngine sharedInstance] getNewMessage:dataSingleton.userInfo.userNo types:nil viewControllerIdentifier:nil success:^(AFHTTPRequestOperation *operation, id responseObject) {
        if ([responseObject isKindOfClass:[TGModelGetNewMessageRsp class]]) {
            TGModelGetNewMessageRsp *rsp = (TGModelGetNewMessageRsp *)responseObject;
            
            if ((rsp.header.status == rspStatus_Succeed)) {
                
                TGMessageDBManager *msgDBManager = [TGMessageDBManager sharedMessageDBManager];
                
                BOOL isInsert = NO;
                BOOL hasDTC = NO;
                
                for (TGModelMessage *message in rsp.messageList__TGModelMessage) {
                    if ([message.type isEqualToString:VEHICLE_FAULT_2_APP]) {
                        hasDTC = YES;
                    }
                    if ([msgDBManager insertNewMessageWithUserNo:dataSingleton.userInfo.userNo message:message]) {
                        isInsert = YES;
                    };
                }
                
                if (isInsert) {
                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_GetNewMessage object:nil];
                    [[NSNotificationCenter defaultCenter] postNotificationName:NOTIFICATION_SetUnreadMessageNum object:nil];
                }
                
                if (hasDTC && isInsert) {
                    [TGAlertView showAlertViewWithTitle:nil message:@"您的爱车出现故障了" leftBtnTitle:@"查看" rightBtnTitle:nil leftHandler:^(SIAlertView *alertView) {
                        [TGAppDelegateSingleton.rootViewController pushViewController:[[TGDTCManagerViewController alloc] init] animated:YES];
                    } rightHandler:nil];
                }
            }
        }
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
        NSLog(@"fail to get new message error---------%@", error);
    }];
}

@end
