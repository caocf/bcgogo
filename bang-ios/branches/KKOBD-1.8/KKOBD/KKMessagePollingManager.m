//
//  KKMessagePollingManager.m
//  KKOBD
//
//  Created by zhuyc on 13-9-18.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKMessagePollingManager.h"
#import "KKPreference.h"
#import "KKCustomAlertView.h"
#import "KKProtocolEngine.h"
#import "KKModelBaseElement.h"
#import "KKModelComplex.h"
#import "KKError.h"
#import "KKGlobal.h"
#import "KKHelper.h"
#import "KKDB.h"
#import "KKTBMessage.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"

static KKMessagePollingManager  *_messageManager;
static NSTimer                  *_pollingTimer = nil;
static KKMsgPlaySound           *_playSoud;

@implementation KKMessagePollingManager

+ (void)startPolling
{    
    if (_messageManager == nil)
        _messageManager = [[KKMessagePollingManager alloc] init];
    
    NSInteger timeInterval = [KKPreference sharedPreference].appConfig.serverReadInterval/1000;
    if (timeInterval == 0)
        timeInterval = 60;
    
    if (_playSoud == nil)
        _playSoud = [[KKMsgPlaySound alloc] initForPlayingSoundEffectWith:@"msgcome.wav"];
    
    [_messageManager getMessages];

    if (_pollingTimer == nil)
        _pollingTimer = [NSTimer scheduledTimerWithTimeInterval:timeInterval
                                                     target:_messageManager
                                                   selector:@selector(getMessages)
                                                   userInfo:nil repeats:YES];
}

- (void)getMessages
{
    [[KKProtocolEngine sharedPtlEngine] messagePollingWithType:nil delegate:self];
}

+ (void)stopPolling
{
    if (_pollingTimer)
        [_pollingTimer invalidate];
    
    _pollingTimer = nil;
}

- (void)pushNotification
{
    if ([KKPreference sharedPreference].voiceSwitch.isOn)
        [_playSoud play];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:KKMessagePollingNotification object:nil];
}

#pragma mark -
#pragma mark KKProtocolEngineDelegate

- (NSNumber *)messagePollingResponse:(NSNumber *)aReqId withObject:(id)aRspObj
{
    NSObject *rsp = (NSObject*)aRspObj;
	if ([rsp isKindOfClass:[KKError class]]) {
        KKError * error = (KKError *)rsp;
//        [KKCustomAlertView showErrorAlertViewWithMessage:error.description block:nil];
        NSLog(@"polling message error is %@",error.description);
		return KKNumberResultEnd;
	}    
    
    KKModelMessagePollingRsp *listRsp = (KKModelMessagePollingRsp *)rsp;
    
    NSMutableArray *addArray = [[NSMutableArray alloc] init];
    
    KKTBMessage *tbMessage = [[KKTBMessage alloc] initWithDB:[KKDB sharedDB]];
    for (KKModelMessage *message in listRsp.KKArrayFieldName(messageList, KKModelMessage))
    {
        BOOL have = [tbMessage isAleradyHaveMessage:message.id andUserNo:[KKProtocolEngine sharedPtlEngine].userName];
        if (have)
            [tbMessage deleteOneMessages:message.id andUserNo:[KKProtocolEngine sharedPtlEngine].userName];
        else
            [addArray addObject:message];
    }
    NSInteger COMMENT_SHOP_Num = 0;
    KKModelMessage *comment_msg = [[KKModelMessage alloc] init];
    for (KKModelMessage *message in addArray) {
        if([message.type isEqualToString:@"COMMENT_SHOP"])
        {
            COMMENT_SHOP_Num++;
            comment_msg.id = message.id;
            comment_msg.title = message.title;
            comment_msg.type = message.type;
            comment_msg.content = message.content;
            comment_msg.actionType = message.actionType;
            comment_msg.params = message.params;
        }
        [tbMessage insertNewMessages:message];
    }
    [tbMessage limt100MsgesForUserNo:[KKProtocolEngine sharedPtlEngine].userName];
    [tbMessage release];
    
    [KKAppDelegateSingleton setNewMsgBadgeValue:KKAppDelegateSingleton.unReadNum + [addArray count]];
    
    if ([addArray count] > 0)
        [self pushNotification];
    
    //弹出评价对话框
    if(COMMENT_SHOP_Num > 0)
    {
        KKCustomAlertView *alertView = [[KKCustomAlertView alloc] initWithMessage:@"评价提醒" WithType:KKCustomAlertView_default];
        [alertView addButtonWithTitle:@"我知道了" imageName:@"alert-blue2-button.png" block:nil];
        [alertView addButtonWithTitle:@"去评价！" imageName:@"alert-orange-button.png" block:^{
            [[NSNotificationCenter defaultCenter] postNotificationName:@"jumpToReviewViewController" object:comment_msg];
        }];
        [alertView show];
        [alertView release];
    }
    [comment_msg release];
    
    [addArray release];
    
    return KKNumberResultEnd;
}

- (void)dealloc
{
    if (_messageManager)
        [_messageManager release];
    _messageManager = nil;
    
    [_playSoud release];
    _playSoud = nil;
    
    _pollingTimer = nil;
    [super dealloc];
}
@end
