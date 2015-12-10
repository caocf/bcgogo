//
//  KKMessagePollingManager.h
//  KKOBD
//
//  Created by zhuyc on 13-9-18.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKProtocolEngineDelegate.h"
#import "KKMsgPlaySound.h"

#define KKMessagePollingNotification @"messageUpdateNotification"

@interface KKMessagePollingManager : NSObject<KKProtocolEngineDelegate>

+ (void)startPolling;
+ (void)stopPolling;

@end
