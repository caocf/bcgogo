//
//  KKModelPreferenceObject.m
//  KKOBD
//
//  Created by zhuyc on 13-9-4.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKModelPreferenceObject.h"

@implementation KKModelPreferenceUserInfo

- (void)dealloc
{
    self.userNo = nil;
    self.password = nil;
    self.mobile = nil;
    self.username = nil;
    [super dealloc];
}

@end

@implementation KKModelPreferencePromptVoiceSwitch

@end


@implementation KKModelPreferenceDefaultPeripheral

- (void)dealloc
{
    self.peripheralName = nil;
    self.peripheralUUID = nil;
    [super dealloc];
}

@end

@implementation KKModelPreferenceGlobalValue

- (void)dealloc
{
    self.currentVehicleMile =  nil;
    [super dealloc];
}

@end

@implementation KKModelPreferenceCityInfo

- (void)dealloc
{
    self.provinceName = nil;
    self.cityName = nil;
    self.cityCode = nil;
    self.latitude = nil;
    self.longitude = nil;
    
    [super dealloc];
}

@end