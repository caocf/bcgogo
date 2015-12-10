//
//  KKModelPreferenceObject.h
//  KKOBD
//
//  Created by zhuyc on 13-9-4.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKModelComplex.h"

typedef KKModelAppConfig KKModelPreferenceAppConfig;

@interface KKModelPreferenceUserInfo : NSObject{
    
}
@property(nonatomic, copy) NSString	*userNo;
@property(nonatomic, copy) NSString	*password;
@property(nonatomic, copy) NSString	*mobile;
@property(nonatomic, copy) NSString *username;

@end

@interface KKModelPreferencePromptVoiceSwitch : NSObject
@property (nonatomic ,assign)BOOL   isOn;
@property (nonatomic ,assign)BOOL   highLevelWarn;
@property (nonatomic ,assign)BOOL   lowLevelWarn;

@end


@interface KKModelPreferenceDefaultPeripheral : NSObject
@property (nonatomic ,copy) NSString *peripheralName;
@property (nonatomic ,copy) NSString *peripheralUUID;

@end

@interface KKModelPreferenceGlobalValue : NSObject
@property (nonatomic ,copy) NSString *currentVehicleMile;
@property (nonatomic ,assign) BOOL isNotAutoLogin;

@end

@interface KKModelPreferenceCityInfo : NSObject
@property (nonatomic ,copy) NSString  *provinceName;
@property (nonatomic ,copy) NSString  *cityName;
@property (nonatomic ,copy) NSString  *cityCode;
@property (nonatomic ,copy) NSString  *latitude;
@property (nonatomic ,copy) NSString  *longitude;

@end