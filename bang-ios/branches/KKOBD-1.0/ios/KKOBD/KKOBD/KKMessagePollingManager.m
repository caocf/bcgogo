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
    for (KKModelMessage *message in addArray) {
        [tbMessage insertNewMessages:message];
    }
    [tbMessage limt100MsgesForUserNo:[KKProtocolEngine sharedPtlEngine].userName];
    [tbMessage release];
    
    [KKAppDelegateSingleton setNewMsgBadgeValue:KKAppDelegateSingleton.unReadNum + [addArray count]];
    
    if ([addArray count] > 0)
        [self pushNotification];
    
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
