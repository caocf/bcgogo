//
//  BGTask_DTCUpload.m
//  KKOBD
//
//  Created by Jiahai on 14-2-25.
//  Copyright (c) 2014年 Bcgogo. All rights reserved.
//

#import "BGTask_DTCUpload.h"
#import "KKProtocolEngine.h"
#import "KKTBDTCMessage.h"
#import "KKAppDelegate.h"
#import "KKApplicationDefine.h"

@implementation BGTask_DTCUpload

- (void)performTask
{
    KKTBDTCMessage *dtcDB = [[KKTBDTCMessage alloc] initWithDB:[KKDB sharedDB]];
    NSArray *array = [dtcDB getDTCMessageByUserNo:[KKProtocolEngine sharedPtlEngine].userName vehicleModelId:KKAppDelegateSingleton.currentVehicle.vehicleModelId];
    [[KKProtocolEngine sharedPtlEngine] vehicleMultiFault:array delegate:nil];
    [dtcDB release];
}

@end
