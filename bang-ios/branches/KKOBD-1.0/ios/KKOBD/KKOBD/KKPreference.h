//
//  KKPreference.h
//  KKOBD
//
//  Created by zhuyc on 13-8-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "KKModelPreferenceObject.h"

typedef enum {
	eUserInfo,                          // KKModelPreferenceUserInfo
    eAppConfig,                         // KKModelAppConfig
    eVoicePrompt,                       // KKModelPreferencePromptVoiceSwitch
    ePeripheral,                        // KKModelPreferenceDefaultPeripheral
    eGlobalValue,                       // KKModelPreferenceGlobalValue
    eCityInfo                           // KKModelPreferenceCityInfo
} KKPreferenceEnum;

// ========================================================================================================

@interface KKPreference : NSObject
@property(nonatomic ,copy)KKModelPreferenceUserInfo *userInfo;
@property(nonatomic ,copy)KKModelPreferenceAppConfig *appConfig;
@property(nonatomic ,copy)KKModelPreferencePromptVoiceSwitch  *voiceSwitch;
@property(nonatomic ,copy)KKModelPreferenceDefaultPeripheral  *peripheral;
@property(nonatomic ,copy)KKModelPreferenceGlobalValue  *globalValues;
@property(nonatomic ,copy)KKModelPreferenceCityInfo *cityInfo;

- (NSObject *)getPreference:(KKPreferenceEnum)aPreference;
+ (KKPreference *)sharedPreference;
-(NSMutableArray *)userInfoArray;
-(void) removeSavedUserInfo:(KKModelPreferenceUserInfo *)uInfo;

@end
