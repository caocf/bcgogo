//
//  KKPreference.m
//  KKOBD
//
//  Created by zhuyc on 13-8-9.
//  Copyright (c) 2013年 zhuyc. All rights reserved.
//

#import "KKPreference.h"

// ========================================================================================================
static KKPreference *g_preference = nil;

@implementation KKPreference

+ (KKPreference *)sharedPreference
{
    @synchronized(self)
    {
        if (g_preference == nil)
            g_preference = [[[KKPreference alloc] init] autorelease];
    }
    return g_preference;
}

- (void)dealloc
{
    g_preference = nil;
    
    [super dealloc];
}

- (NSObject *)getPreference:(KKPreferenceEnum)aPreference
{
    if (aPreference == eUserInfo)
        return self.userInfo;
    if (aPreference == eAppConfig)
        return self.appConfig;
    if (aPreference == eVoicePrompt)
        return self.voiceSwitch;
    if (aPreference == ePeripheral)
        return self.peripheral;
    if (aPreference == eGlobalValue)
        return self.globalValues;
    if (aPreference == eCityInfo)
        return self.cityInfo;
    
    return nil;
}

//-------------------------------------userInfo------------------------------------------------
- (void)setUserInfo:(KKModelPreferenceUserInfo *)aUserInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == aUserInfo)
    {
        [userDefault removeObjectForKey:@"userInfo"];
        [userDefault synchronize];
        
		return;
    }
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];

    if ([aUserInfo.userNo length] > 0)
        [dict setObject:aUserInfo.userNo forKey:@"userNo"];
    if ([aUserInfo.password length] > 0)
        [dict setObject:aUserInfo.password forKey:@"password"];
    if ([aUserInfo.username length] > 0)
        [dict setObject:aUserInfo.username forKey:@"userName"];
    if ([aUserInfo.mobile length] > 0)
        [dict setObject:aUserInfo.mobile forKey:@"mobile"];
    
    [userDefault setObject:dict forKey:@"userInfo"];
    
	[userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceUserInfo *)userInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"userInfo"];
    
	KKModelPreferenceUserInfo *aUserInfo = [[KKModelPreferenceUserInfo alloc] init];
    aUserInfo.userNo = [dict objectForKey:@"userNo"];
    aUserInfo.password = [dict objectForKey:@"password"];
    aUserInfo.username = [dict objectForKey:@"userName"];
    aUserInfo.mobile = [dict objectForKey:@"mobile"];
    
    return [aUserInfo autorelease];
}

//-------------------------------------appConfig------------------------------------------------

- (void)setAppConfig:(KKModelPreferenceAppConfig *)appConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == appConfig)
    {
        [userDefault removeObjectForKey:@"appConfig"];
        [userDefault synchronize];
        
		return;
    }
    
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    
    if (appConfig.obdReadInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.obdReadInterval] forKey:@"obdReadInterval"];
    if (appConfig.serverReadInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.serverReadInterval] forKey:@"serverReadInterval"];
    if (appConfig.mileageInformInterval > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.mileageInformInterval] forKey:@"mileageInformInterval"];
    if ([appConfig.customerServicePhone length] > 0)
        [dict setObject:[NSString stringWithFormat:@"%@",appConfig.customerServicePhone] forKey:@"customerServicePhone"];
    if (appConfig.appVehicleErrorCodeWarnIntervals > 0)
        [dict setObject:[NSString stringWithFormat:@"%d",appConfig.appVehicleErrorCodeWarnIntervals] forKey:@"appVehicleErrorCodeWarnIntervals"];
    if ([appConfig.remainOilMassWarn length] > 0)
        [dict setObject:appConfig.remainOilMassWarn forKey:@"remainOilMassWarn"];
    
    [userDefault setObject:dict forKey:@"appConfig"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceAppConfig *)appConfig
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"appConfig"];
    
    KKModelPreferenceAppConfig *aAppConfig = [[KKModelPreferenceAppConfig alloc] init];
    aAppConfig.obdReadInterval = ([[dict objectForKey:@"obdReadInterval"] intValue] != 0) ? [[dict objectForKey:@"obdReadInterval"] intValue] : 60000;
    aAppConfig.serverReadInterval = ([[dict objectForKey:@"serverReadInterval"] intValue] != 0) ? [[dict objectForKey:@"serverReadInterval"] intValue] : 60000;
    aAppConfig.mileageInformInterval = ([[dict objectForKey:@"mileageInformInterval"] intValue] != 0) ? [[dict objectForKey:@"mileageInformInterval"] intValue] : 100;
    aAppConfig.customerServicePhone = [dict objectForKey:@"customerServicePhone"];
    aAppConfig.appVehicleErrorCodeWarnIntervals = ([[dict objectForKey:@"appVehicleErrorCodeWarnIntervals"] intValue] != 0) ? [[dict objectForKey:@"appVehicleErrorCodeWarnIntervals"] intValue] : 24;
    aAppConfig.remainOilMassWarn = ([[dict objectForKey:@"remainOilMassWarn"] length] > 0) ? [dict objectForKey:@"remainOilMassWarn"] : @"15_25";
    
    return [aAppConfig autorelease];
}


//---------------------------voicePrompt----------------------------------------------------------------

- (void)setVoiceSwitch:(KKModelPreferencePromptVoiceSwitch *)voiceSwitch
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == voiceSwitch)
    {
        [userDefault removeObjectForKey:@"voicePrompt"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.isOn] forKey:@"isOn"];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.highLevelWarn] forKey:@"highLevelWarn"];
    [dict setObject:[NSNumber numberWithBool:voiceSwitch.lowLevelWarn] forKey:@"lowLevelWarn"];
    
    [userDefault setObject:dict forKey:@"voicePrompt"];
    [userDefault synchronize];
	[dict release];
    
}

- (KKModelPreferencePromptVoiceSwitch *)voiceSwitch
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"voicePrompt"];
    
    KKModelPreferencePromptVoiceSwitch *vcSwitch = [[KKModelPreferencePromptVoiceSwitch alloc] init];
    if (dict == nil)
    {
        vcSwitch.isOn = YES;
    }
    else
    {
        vcSwitch.isOn = [[dict objectForKey:@"isOn"] boolValue];
        vcSwitch.highLevelWarn = [[dict objectForKey:@"highLevelWarn"] boolValue];
        vcSwitch.lowLevelWarn = [[dict objectForKey:@"lowLevelWarn"] boolValue];
    }
    
    return [vcSwitch autorelease];
}


//--------------------------------------------------peripheral----------------------------------------------------------------

- (void)setPeripheral:(KKModelPreferenceDefaultPeripheral *)peripheral
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == peripheral)
    {
        [userDefault removeObjectForKey:@"peripheral"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    [dict setObject:peripheral.peripheralName forKey:@"peripheralName"];
    [dict setObject:peripheral.peripheralUUID forKey:@"peripheralUUID"];
    [userDefault setObject:dict forKey:@"peripheral"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceDefaultPeripheral *)peripheral
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"peripheral"];
    
    KKModelPreferenceDefaultPeripheral *per = [[KKModelPreferenceDefaultPeripheral alloc] init];
    per.peripheralName = [dict objectForKey:@"peripheralName"];
    per.peripheralUUID = [dict objectForKey:@"peripheralUUID"];
    
    return [per autorelease];
}


//--------------------------------------------------GlobalValues-----------------------------------------------------
- (void)setGlobalValues:(KKModelPreferenceGlobalValue *)globalValues
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == globalValues)
    {
        [userDefault removeObjectForKey:@"globalValues"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    if ([globalValues.currentVehicleMile length] > 0)
        [dict setObject:globalValues.currentVehicleMile forKey:@"currentVehicleMile"];
    [dict setObject:[NSNumber numberWithBool:globalValues.isNotAutoLogin] forKey:@"isNotAutoLogin"];
    
    [userDefault setObject:dict forKey:@"globalValues"];
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceGlobalValue *)globalValues
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"globalValues"];
    
    KKModelPreferenceGlobalValue *values = [[KKModelPreferenceGlobalValue alloc] init];
    if ([[dict objectForKey:@"currentVehicleMile"] length] > 0)
        values.currentVehicleMile = [dict objectForKey:@"currentVehicleMile"];
    else
        values.currentVehicleMile = @"-1";
    
    values.isNotAutoLogin = [[dict objectForKey:@"isNotAutoLogin"] boolValue];
    
    return [values autorelease];
}


//-------------------------------------cityInfo-----------------------------------------------------------------

- (void)setCityInfo:(KKModelPreferenceCityInfo *)cityInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
    if (nil == cityInfo)
    {
        [userDefault removeObjectForKey:@"cityInfo"];
        [userDefault synchronize];
        
		return;
    }
    NSMutableDictionary *dict = [[NSMutableDictionary alloc] initWithCapacity:6];
    if ([cityInfo.provinceName length] > 0)
        [dict setObject:cityInfo.provinceName forKey:@"provinceName"];
    if ([cityInfo.cityName length] > 0)
        [dict setObject:cityInfo.cityName forKey:@"cityName"];
    if ([cityInfo.cityCode length] > 0)
        [dict setObject:cityInfo.cityCode forKey:@"cityCode"];
    if ([cityInfo.latitude length] > 0)
        [dict setObject:cityInfo.latitude forKey:@"latitude"];
    if ([cityInfo.longitude length] > 0)
        [dict setObject:cityInfo.longitude forKey:@"longitude"];
    
    [userDefault setObject:dict forKey:@"cityInfo"];
    
    [userDefault synchronize];
	[dict release];
}

- (KKModelPreferenceCityInfo *)cityInfo
{
    NSUserDefaults *userDefault = [NSUserDefaults standardUserDefaults];
	NSDictionary *dict = [userDefault objectForKey:@"cityInfo"];
    
    KKModelPreferenceCityInfo *cityInfo = [[KKModelPreferenceCityInfo alloc] init];
    cityInfo.provinceName = [dict objectForKey:@"provinceName"];
    cityInfo.cityName = [dict objectForKey:@"cityName"];
    cityInfo.cityCode = [dict objectForKey:@"cityCode"];
    cityInfo.latitude = [dict objectForKey:@"latitude"];
    cityInfo.longitude = [dict objectForKey:@"longitude"];
    
    return [cityInfo autorelease];
}
@end




